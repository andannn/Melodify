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
    namespace = "com.andannn.melodify.ui.routes.library"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:components:library-item"))
            implementation(project(":shared:compose:components:library-detail"))
            implementation(project(":mobile-ui:common"))
        }
    }
}
