plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.core.syncer"
    }
}
