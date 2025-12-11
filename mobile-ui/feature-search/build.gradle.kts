plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify.ui.routes.search"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared:compose:components:search"))
            implementation(project(":mobile-ui:common"))
        }
    }
}
