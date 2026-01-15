plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
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
            implementation(project(":shared:syncer:api"))

            implementation(libs.coil3.compose)
        }
    }
}
