plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    id("kmp.ext")
}

kmpExt {
    withAndroid {
        enableDeviceTest = true
    }
    withIOS()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.util.volumn"
    }
}
