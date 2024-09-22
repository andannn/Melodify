plugins {
    id("musicplayer.android.library")
    id("musicplayer.android.hilt")
}

android {
    namespace = "com.andanana.melodify.feature.core.player"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:mediastore"))

    implementation(libs.kotlinx.coroutines.guava)

    // Media3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
}
