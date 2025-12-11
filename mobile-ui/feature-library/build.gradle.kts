plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify.ui.routes.library"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared:ui"))
            implementation(project(":mobile-ui:common"))
        }
    }
}
