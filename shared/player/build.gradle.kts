plugins {
    id("melodify.kmp.library")
}

android {
    namespace = "com.andannn.melodify.ui.core.player"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:player-common"))
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.guava)

            // Media3
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.session)
            implementation(libs.androidx.media3.common)
        }

        desktopMain.dependencies {
            implementation(libs.vlcj)
            api(project(":shared:player-none-android-api"))
        }

        iosMain.dependencies {
            implementation(project(":shared:player-ios"))
            api(project(":shared:player-none-android-api"))
        }
    }
}
