import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        StartKoinHelperKt.startKoinWithPlatformModule {
            SwiftInteropModuleKt.swiftInteropModule(mPMediaScanner: MPMediaScannerImpl(),
                                                    musicLibraryPermissionHandler: MusicLibraryPermissionHandlerImpl(),
                                                    avPlayerWrapper: AVPlayerWrapperImpl(),
                                                    screenOrientationController: ScreenOrientationControllerImpl(),
                                                    mediaArtworkViewControllerFactory: MediaArtworkViewControllerFactoryImpl())
        }
    }
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) private var appDelegate
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}


class AppDelegate : NSObject, UIApplicationDelegate {
    static var orientation: UIInterfaceOrientationMask = .all

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        return true
    }

    func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
        return Self.orientation
    }
}
