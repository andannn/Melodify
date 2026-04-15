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
    android {
        namespace = "com.andannn.melodify.ui.routes.home"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:syncer:api"))
            implementation(project(":shared:compose:components:tab"))
            implementation(project(":shared:compose:components:play-control"))
            implementation(project(":shared:compose:components:tab-content"))
            implementation(project(":shared:compose:components:search"))
            implementation(project(":mobile-ui:common"))
            implementation(project(":shared:compose:popup:dialog:entry:option"))
            implementation(project(":shared:compose:popup:dialog:entry:sync"))
            implementation(project(":shared:compose:popup:dialog:entry:sort-rule"))
        }
    }
}
