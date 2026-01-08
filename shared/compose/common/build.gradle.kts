plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withIOS()
    withDesktop()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.shared.compose.common"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:compose:resource"))
            api(project(":shared:domain:api"))
            api(libs.retainedmodel)
            implementation(libs.coil3.compose)
            implementation(libs.reorderable)
        }

        iosMain.dependencies {
            implementation(project(":shared:util:artwork-ios"))
            implementation(project(":shared:syncer:platform-ios"))
        }
    }
}
