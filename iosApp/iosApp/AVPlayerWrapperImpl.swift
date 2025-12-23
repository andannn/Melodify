import ComposeApp
import AVFoundation
import Foundation

class AVPlayerWrapperImpl: NSObject, AVPlayerWrapper {
    
    // MARK: - AVPlayer 内部状态
    
    private var player: AVPlayer?
    private var timeObserver: Any?
    
    // Kotlin 侧会读取这两个值
    var currentDurationMs: Int64 = 0
    var currentPositionMs: Int64 = 0
    
    // Kotlin 设置的回调
    var onCompleted: (() -> Void)?
    var onProgress: ((KotlinLong, KotlinLong) -> Void)?
    
    override init() {
        super.init()
        setupAudioSession()
    }
    
    private func setupAudioSession() {
        do {
            try AVAudioSession.sharedInstance().setCategory(.playback, mode: .default)
            try AVAudioSession.sharedInstance().setActive(true)
        } catch {
            print("Audio session error: \(error)")
        }
    }
    
    // MARK: - AVPlayerWrapper 接口实现
    
    func playUrl(url: String) {
        stop()
        
        guard let u = URL(string: url) else {
            print("AVPlayerWrapperImpl: invalid url \(url)")
            return
        }
        
        let item = AVPlayerItem(url: u)
        player = AVPlayer(playerItem: item)
        
        // 播放完成通知
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(playerItemDidFinish(_:)),
            name: .AVPlayerItemDidPlayToEndTime,
            object: item
        )
        
        addTimeObserver()
        
        player?.play()
    }
    
    func pause() {
        player?.pause()
    }
    
    func resume() {
        player?.play()
    }

    func setPlayBackSpeed(speed: Float) {
        player?.rate = speed
    }
    
    func seekTo(positionMs: Int64) {
        guard let player = player else { return }
        let seconds = Double(positionMs) / 1000.0
        let time = CMTime(seconds: seconds, preferredTimescale: 600)
        player.seek(to: time)
    }
    
    func stop() {
        // 移除进度观察者
        if let observer = timeObserver {
            player?.removeTimeObserver(observer)
            timeObserver = nil
        }
        
        // 移除完成通知
        NotificationCenter.default.removeObserver(self, name: .AVPlayerItemDidPlayToEndTime, object: player?.currentItem)
        
        player?.pause()
        player = nil
        
        // 清零状态
        currentPositionMs = 0
        currentDurationMs = 0
    }
    
    // MARK: - 内部辅助
    
    private func addTimeObserver() {
        guard let player = player else { return }
        
        // 防止重复添加
        if timeObserver != nil {
            player.removeTimeObserver(timeObserver as Any)
            timeObserver = nil
        }
        
        let interval = CMTime(seconds: 0.3, preferredTimescale: 600)
        
        timeObserver = player.addPeriodicTimeObserver(
            forInterval: interval,
            queue: .main
        ) { [weak self] time in
            guard let self = self else { return }
            guard let item = player.currentItem else { return }
            
            let posSec = CMTimeGetSeconds(time)
            let durSec = CMTimeGetSeconds(item.duration)
            
            // 处理 NaN / inf
            guard posSec.isFinite, durSec.isFinite, durSec > 0 else {
                return
            }
            
            let posMs = Int64(posSec * 1000.0)
            let durMs = Int64(durSec * 1000.0)
            
            self.currentPositionMs = posMs
            self.currentDurationMs = durMs

            // 回调给 Kotlin
            if let progressCallback = self.onProgress {
                // KotlinLong 的构造方法可能是 init(value:) 或 init(_:)
                // 如果报错，按 Xcode 自动补全改一下这里即可
                progressCallback(KotlinLong(value: posMs), KotlinLong(value: durMs))
            }
        }
    }
    
    @objc
    private func playerItemDidFinish(_ notification: Notification) {
        currentPositionMs = currentDurationMs

        // 告诉 Kotlin “播完了”
        onCompleted?()
    }
    
    deinit {
        stop()
        NotificationCenter.default.removeObserver(self)
    }
}
