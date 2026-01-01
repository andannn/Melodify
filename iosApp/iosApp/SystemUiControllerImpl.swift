import ComposeApp
import SwiftUI
import UIKit

class SystemUiControllerImpl: SystemUiController {
    // 弱引用持有 RootVC，防止内存泄漏
    private weak var rootViewController: RootViewController?

    init(rootViewController: RootViewController) {
        self.rootViewController = rootViewController
    }

    // 1. 设置可见性
    func setSystemUiVisibility(visible: Bool) {
        rootViewController?.isStatusBarHidden = !visible
        updateSystemUi()
    }

    // 2. 设置沉浸模式 (隐藏状态栏 + 隐藏底部横条)
    func setImmersiveModeEnabled(enable: Bool) {
        rootViewController?.isStatusBarHidden = enable
        rootViewController?.isHomeIndicatorAutoHidden = enable
        updateSystemUi()
    }

    // 3. 设置暗黑/亮色主题 (控制图标颜色)
    func setSystemUiDarkTheme(isDark: Bool) {
        if isDark {
            // App 是暗色背景 -> 需要白色文字/图标
            rootViewController?.currentStatusBarStyle = .lightContent
        } else {
            // App 是亮色背景 -> 需要黑色文字/图标
            if #available(iOS 13.0, *) {
                rootViewController?.currentStatusBarStyle = .darkContent
            } else {
                // iOS 12 及以下不支持黑色图标，只能退回默认
                rootViewController?.currentStatusBarStyle = .default
            }
        }
        updateSystemUi()
    }

    // 4. 自动/跟随系统
    func setSystemUiStyleAuto() {
        rootViewController?.currentStatusBarStyle = .default
        updateSystemUi()
    }

    // 核心：通知系统去刷新
    private func updateSystemUi() {
        // 动画更新状态栏
        UIView.animate(withDuration: 0.3) {
            self.rootViewController?.setNeedsStatusBarAppearanceUpdate()
            self.rootViewController?.setNeedsUpdateOfHomeIndicatorAutoHidden()
        }
    }
}
