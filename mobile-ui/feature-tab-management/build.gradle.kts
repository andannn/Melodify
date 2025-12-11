plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify.ui.routes.tag.management"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared:ui"))
            implementation(project(":mobile-ui:common"))
            implementation(project(":mobile-ui:feature-player"))
        }
    }
}
