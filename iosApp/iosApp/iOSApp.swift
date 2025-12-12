import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        StartKoinHelperKt.startKoinWithPlatformModule {
            SwiftInteropModuleKt.swiftInteropModule(mPMediaScanner: MPMediaScannerImpl(),
                                                    musicLibraryPermissionHandler: MusicLibraryPermissionHandlerImpl(),
                                                    avPlayerWrapper: AVPlayerWrapperImpl(),
                                                    screenOrientationController: ScreenOrientationControllerImpl())
        }
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
