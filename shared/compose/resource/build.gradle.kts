

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withIOSTarget()
    withDesktopTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.shared.compose.resource"
        androidResources.enable = true
    }
}

compose.resources {
    publicResClass = true
    generateResClass = auto
}
