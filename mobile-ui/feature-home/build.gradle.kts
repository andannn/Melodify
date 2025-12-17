import com.android.build.api.dsl.androidLibrary

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
        namespace = "com.andannn.melodify.ui.routes.home"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:syncer:impl"))
            implementation(project(":shared:compose:components:tab"))
            implementation(project(":shared:compose:components:tab-content"))
            implementation(project(":mobile-ui:common"))
        }
    }
}
