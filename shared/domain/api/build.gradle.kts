plugins {
    alias(libs.plugins.android.library)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withDesktop()
    withIOS()
}

android {
    namespace = "com.andannn.melodify.domain"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.paging.common)
        }
    }
}
