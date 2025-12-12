import ComposeApp
import MediaPlayer

class MusicLibraryPermissionHandlerImpl : MusicLibraryPermissionHandler {
    func mediaPermissionGranted() -> Bool {
        MPMediaLibrary.authorizationStatus() == .authorized
    }
    
    func requestMusicLibraryAuthorization(completion: @escaping (KotlinBoolean) -> Void) {
        let status = MPMediaLibrary.authorizationStatus()
        switch status {
        case .authorized:
            completion(true)

        case .notDetermined:
            MPMediaLibrary.requestAuthorization { newStatus in
                DispatchQueue.main.async {
                    completion(KotlinBoolean(bool: newStatus == .authorized))
                }
            }

        default:
            completion(false)
        }
    }
}
