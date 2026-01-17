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
        namespace = "com.andannn.melodify.shared.compose.popup.entry.option"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:common"))
            api(project(":shared:compose:popup:dialog:controller"))
        }
    }
}
