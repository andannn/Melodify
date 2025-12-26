
import ComposeApp
import UIKit

final class ScreenOrientationControllerImpl: ScreenOrientationController {
    private var _isRequestLandScape: Bool = false
    
    func cancelRequest() {
        if let windowScene  = (UIApplication.shared.connectedScenes.first as? UIWindowScene) {
            _isRequestLandScape = false

            AppDelegate.orientation = .all
            windowScene.requestGeometryUpdate(.iOS(interfaceOrientations: .all))
            windowScene.keyWindow?.rootViewController?.setNeedsUpdateOfSupportedInterfaceOrientations()
        }
    }

    func isRequestLandscape() -> Bool {
        return _isRequestLandScape
    }
    
    func requestLandscape() {
        if let windowScene  = (UIApplication.shared.connectedScenes.first as? UIWindowScene) {
            _isRequestLandScape = true

            AppDelegate.orientation = .landscape
            windowScene.requestGeometryUpdate(.iOS(interfaceOrientations: .landscape))
            windowScene.keyWindow?.rootViewController?.setNeedsUpdateOfSupportedInterfaceOrientations()
        }
    }
    
    var isCurrentPortrait: Bool {
        if let windowScene  = (UIApplication.shared.connectedScenes.first as? UIWindowScene) {
            return windowScene.interfaceOrientation.isPortrait
        }

        return true
    }
}
