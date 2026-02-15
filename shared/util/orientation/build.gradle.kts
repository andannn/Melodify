plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kotlin {
    android {
        namespace = "com.andannn.melodify.util.orientation"
    }
}

kmpExt {
    withAndroidTarget()
    withIOSTarget()
}
