plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.andannn.melodify.ui.core.player"
    compileSdk = libs.versions.android.compileSdk.get().toString().toInt()
}

dependencies {
    api(project(":shared:player:common"))
    implementation(project(":shared:network:service:siren"))

    implementation(project(":shared:player:sleep-timer"))
    implementation(libs.kotlinx.coroutines.guava)

    implementation(libs.napier)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    // Media3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    api(libs.androidx.media3.common)
}

