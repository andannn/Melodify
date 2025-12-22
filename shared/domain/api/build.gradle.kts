

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withDesktop()
    withIOS()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.domain"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.paging.common)
        }
    }
}
