plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withIOSTarget()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.ui.common"
    }
    sourceSets {
        commonMain.dependencies {
            api(libs.navigation3.runtime)
            api(project(":shared:compose:common"))
            api(project(":shared:compose:usecase"))
            api(project(":shared:compose:popup:dialog:common"))
            api(project(":shared:compose:popup:snack-bar"))
            api(project(":shared:util:orientation"))
            api(project(":shared:util:immersive"))
        }
    }
}
