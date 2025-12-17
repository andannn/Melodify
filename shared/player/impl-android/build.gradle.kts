import com.android.build.api.dsl.androidLibrary

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.ui.core.player"
    }

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
