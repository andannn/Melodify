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
        namespace = "com.andannn.melodify.shared.compose.components.queue"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:usecase"))
            implementation(project(":shared:domain:api"))

            implementation(libs.reorderable)
        }
    }
}
