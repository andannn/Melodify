

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withIOS()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.ui.app"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.navigation3.ui)
            implementation(project(":mobile-ui:common"))
            implementation(project(":mobile-ui:feature-player"))
            implementation(project(":mobile-ui:feature-home"))
            implementation(project(":mobile-ui:feature-library"))
            implementation(project(":mobile-ui:feature-search"))
            implementation(project(":mobile-ui:feature-tab-management"))
        }
    }
}
