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
        commonMain.dependencies {
            implementation(project(":shared:compose:components:tab-management"))
            implementation(project(":mobile-ui:common"))
        }
    }
}
