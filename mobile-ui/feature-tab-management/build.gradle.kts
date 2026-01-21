

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
        namespace = "com.andannn.melodify.ui.routes.tag.management"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:components:tab-management"))
            implementation(project(":mobile-ui:common"))
        }
    }
}
