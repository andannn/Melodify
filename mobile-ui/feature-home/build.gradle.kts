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
            implementation(project(":shared:ui"))
            implementation(project(":shared:syncer"))
            implementation(project(":mobile-ui:common"))
        }
    }
}
