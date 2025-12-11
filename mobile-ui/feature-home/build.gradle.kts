plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
}

android {
    namespace = "com.andannn.melodify.ui.routes.home"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared:syncer"))
            implementation(project(":shared:compose:components:tab"))
            implementation(project(":shared:compose:components:tab-content"))
            implementation(project(":mobile-ui:common"))
        }
    }
}
