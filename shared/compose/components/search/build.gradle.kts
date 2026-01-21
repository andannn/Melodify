plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withIOSTarget()
    withDesktopTarget()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.shared.compose.components.search"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:usecase"))
            implementation(project(":shared:compose:components:library-item"))
            implementation(project(":shared:domain:api"))
        }
    }
}
