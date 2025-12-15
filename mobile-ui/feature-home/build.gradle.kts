plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withIOS()
}

android {
    namespace = "com.andannn.melodify.ui.routes.home"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:syncer:impl"))
            implementation(project(":shared:compose:components:tab"))
            implementation(project(":shared:compose:components:tab-content"))
            implementation(project(":mobile-ui:common"))
        }
    }
}
