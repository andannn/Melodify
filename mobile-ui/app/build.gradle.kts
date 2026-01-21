

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withIOSTarget()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.ui.app"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.navigation3.ui)
            implementation(project(":mobile-ui:common"))
            implementation(project(":mobile-ui:feature-player"))
            implementation(project(":mobile-ui:feature-home"))
            implementation(project(":mobile-ui:feature-library"))
            implementation(project(":mobile-ui:feature-search"))
            implementation(project(":mobile-ui:feature-tab-management"))
            implementation(project(":shared:compose:popup:dialog:entry:option"))
            implementation(project(":shared:compose:popup:dialog:entry:sort-rule"))
            implementation(project(":shared:compose:popup:dialog:entry:option"))
            implementation(project(":shared:compose:popup:dialog:entry:sort-rule"))
            implementation(project(":shared:compose:popup:dialog:entry:alert"))
            implementation(project(":shared:compose:popup:dialog:entry:sleep-timer"))
            implementation(project(":shared:compose:popup:dialog:entry:sync"))
            implementation(project(":shared:compose:popup:dialog:entry:play-list"))
        }
    }
}
