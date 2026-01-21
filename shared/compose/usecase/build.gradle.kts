

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
        namespace = "com.andannn.melodify.shared.compose.usecase"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:domain:api"))
            api(project(":shared:compose:common"))
            api(project(":shared:compose:popup:dialog:common"))
            api(project(":shared:compose:popup:snack-bar"))
            implementation(project(":shared:syncer:api"))
            implementation(project(":shared:compose:popup:dialog:entry:option"))
            implementation(project(":shared:compose:popup:dialog:entry:sleep-timer"))
            implementation(project(":shared:compose:popup:dialog:entry:alert"))
            implementation(project(":shared:compose:popup:dialog:entry:play-list"))
        }
    }
}
