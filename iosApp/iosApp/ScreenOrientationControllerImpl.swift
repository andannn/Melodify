
import ComposeApp

class ScreenOrientationControllerImpl: ScreenOrientationController {
    func cancelRequest() {
    }
    
    func isRequestLandscape() -> Bool {
        return false
    }
    
    func requestLandscape() {
        
    }
    
    var isCurrentPortrait: Bool = true
}
