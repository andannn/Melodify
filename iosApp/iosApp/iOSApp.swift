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
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
