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
        namespace = "com.andannn.melodify.shared.compose.popup.entry.sort.rule"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:compose:popup:dialog:controller"))
            implementation(project(":shared:compose:common"))
            implementation(project(":shared:domain:api"))
        }
    }
}
