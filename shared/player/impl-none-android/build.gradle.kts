plugins {
    id("melodify.kmp.library")
}

android {
    namespace = "com.andannn.melodify.ui.core.player"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:player:common"))
        }

        desktopMain.dependencies {
            implementation(libs.vlcj)
        }

        iosMain.dependencies {
            implementation(project(":shared:player:platform-player-ios"))
        }
    }
}
