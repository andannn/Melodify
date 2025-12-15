plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withIOS()
    withDesktop()
}

android {
    namespace = "com.andannn.melodify.shared.compose.components.lyrics"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:usecase"))
            implementation(project(":shared:domain:api"))
            implementation(project(":shared:platform"))
        }
    }
}
