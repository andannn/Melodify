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
        namespace = "com.andannn.melodify.shared.compose.components.play.control"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:usecase"))
            implementation(project(":shared:domain:api"))
            implementation(project(":shared:platform"))
            implementation(project(":shared:compose:popup:dialog:entry:option"))
            implementation(project(":shared:compose:popup:dialog:entry:sleep-timer"))
        }
    }
}
