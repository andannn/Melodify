

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withDesktopTarget()
    withIOSTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.domain"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.paging.common)
        }
    }
}
