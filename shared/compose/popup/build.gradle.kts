import com.android.build.api.dsl.androidLibrary

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withIOS()
    withDesktop()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.shared.compose.popup"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:common"))
            implementation(project(":shared:domain:api"))

            implementation(libs.coil3.compose)
        }
    }
}
