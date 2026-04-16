

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withDesktopTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.core.syncer.scanner"
    }
    sourceSets {
    }
}
