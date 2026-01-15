

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
        namespace = "com.andannn.melodify.ui.routes.library"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:components:library-item"))
            implementation(project(":shared:compose:components:library-detail"))
            implementation(project(":mobile-ui:common"))
            implementation(project(":shared:compose:popup:dialog:entry:option"))
        }
    }
}
