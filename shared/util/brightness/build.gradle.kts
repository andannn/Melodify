

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withIOSTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.util.brightness"
    }

    sourceSets {
        androidMain.dependencies {
        }
    }
}
