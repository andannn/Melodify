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

        androidMain.dependencies {
            implementation(project(":shared:player:sleep-timer"))
            implementation(libs.kotlinx.coroutines.guava)

            // Media3
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.session)
            api(libs.androidx.media3.common)
        }
    }
}
