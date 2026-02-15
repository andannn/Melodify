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
    android {
        namespace = "com.andannn.melodify.shared.compose.popup.entry.sort.rule"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.popuphost)
            api(project(":shared:compose:popup:dialog:common"))
            implementation(project(":shared:compose:common"))
            implementation(project(":shared:domain:api"))
        }
    }
}
