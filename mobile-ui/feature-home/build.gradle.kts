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
        namespace = "com.andannn.melodify.ui.routes.home"
    }
    sourceSets {
        // Workaround for issue: https://github.com/andannn/Melodify/issues/547
        // Only bump up material3 version for android.
        androidMain.dependencies {
            implementation(libs.androidx.compose.material3)
        }
        commonMain.dependencies {
            implementation(project(":shared:syncer:api"))
            implementation(project(":shared:compose:components:tab"))
            implementation(project(":shared:compose:components:play-control"))
            implementation(project(":shared:compose:components:tab-content"))
            implementation(project(":shared:compose:components:search"))
            implementation(project(":mobile-ui:common"))
            implementation(project(":shared:compose:popup:dialog:entry:sync"))
            implementation(project(":shared:compose:popup:dialog:entry:sort-rule"))
        }
    }
}
