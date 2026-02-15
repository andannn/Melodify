plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget {
        enableDeviceTest = true
    }
    withIOSTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.util.volumn"
    }
}
