plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.util.orientation"
    }
}

kmpExt {
    withAndroid()
    withIOS()
}
