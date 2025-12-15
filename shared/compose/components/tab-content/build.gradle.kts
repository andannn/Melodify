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
    namespace = "com.andannn.melodify.shared.compose.components.tab.content"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:usecase"))
            implementation(project(":shared:domain:api"))
            implementation(libs.coil3.compose)
            implementation(libs.androidx.paging.compose)
        }
    }
}
