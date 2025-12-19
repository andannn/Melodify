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
        namespace = "com.andannn.melodify.shared.compose.components.tab.content"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:usecase"))
            implementation(project(":shared:domain:api"))
            implementation(libs.coil3.compose)
            implementation(libs.androidx.paging.compose)
        }
    }
}
