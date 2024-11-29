plugins {
    id("melodify.kmp.library")
}

android {
    namespace = "com.andannn.melodify.feature.core.player"
}

kotlin {

    sourceSets {
        commonMain.dependencies {

        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.guava)

            // Media3
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.session)
        }

        desktopMain.dependencies {
            implementation(libs.vlcj)
        }
    }
}
