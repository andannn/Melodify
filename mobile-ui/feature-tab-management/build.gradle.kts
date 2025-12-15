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
    namespace = "com.andannn.melodify.ui.routes.tag.management"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:components:tab-management"))
            implementation(project(":mobile-ui:common"))
        }
    }
}
