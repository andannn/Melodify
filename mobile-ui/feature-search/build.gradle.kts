plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withIOS()
}

android {
    namespace = "com.andannn.melodify.ui.routes.search"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:components:search"))
            implementation(project(":mobile-ui:common"))
        }
    }
}
