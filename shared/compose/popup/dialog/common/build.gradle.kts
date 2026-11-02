plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.shared.compose.popup.controller"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.popuphost)
        }
    }
}
