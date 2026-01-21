import UIKit
import SwiftUI
import ComposeApp

class RootViewController: UIViewController {
    // 存储当前的状态栏样式
    var currentStatusBarStyle: UIStatusBarStyle = .default
    
    // 存储是否隐藏状态栏
    var isStatusBarHidden: Bool = false
    
    // 存储是否隐藏底部 Home Indicator (沉浸模式用)
    var isHomeIndicatorAutoHidden: Bool = false
    
    // MARK: - iOS 系统回调重写
    
    // 1. 控制状态栏文字颜色
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return currentStatusBarStyle
    }
    
    // 2. 控制状态栏显示/隐藏
    override var prefersStatusBarHidden: Bool {
        return isStatusBarHidden
    }
    
    // 3. 控制底部 Home Bar (iPhone X+) 的隐藏
    override var prefersHomeIndicatorAutoHidden: Bool {
        return isHomeIndicatorAutoHidden
    }
}

struct RootViewControllerRepresentable: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let rootVC = RootViewController()
        let systemUiController = SystemUiControllerImpl(rootViewController: rootVC)

#if SIREN
        let scannerType = ApiScannerType.monsterSiren
#else
        let scannerType = ApiScannerType.local
#endif

        StartKoinHelperKt.startKoinWithPlatformModule(scannerType: scannerType) {
            SwiftInteropModuleKt.swiftInteropModule(
                mPMediaScanner: MPMediaScannerImpl(),
                musicLibraryPermissionHandler: MusicLibraryPermissionHandlerImpl(),
                avPlayerWrapper: AVPlayerWrapperImpl(),
                screenOrientationController: ScreenOrientationControllerImpl(),
                mediaArtworkViewControllerFactory: MediaArtworkViewControllerFactoryImpl(),
                systemUiController: systemUiController)
        }
        let composeVC = MainControllerKt.MainViewController()
        rootVC.addChild(composeVC)
        rootVC.view.addSubview(composeVC.view)
        
        composeVC.view.frame = rootVC.view.bounds
        composeVC.didMove(toParent: rootVC)
        return rootVC
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}


struct ContentView: View {
    var body: some View {
        RootViewControllerRepresentable()
            .ignoresSafeArea()
    }
}
