

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
        namespace = "com.andannn.melodify.ui.routes.search"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:components:search"))
            implementation(project(":mobile-ui:common"))
        }
    }
}
