

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withIOSTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.util.immersive"
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}
