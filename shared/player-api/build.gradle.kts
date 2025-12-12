plugins {
    id("melodify.kmp.library")
}

android {
    namespace = "com.andannn.melodify.ui.core.player.api"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            api(libs.androidx.media3.common)
        }

        desktopMain.dependencies {
            api(libs.vlcj)
        }
    }
}
