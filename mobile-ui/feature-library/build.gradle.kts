plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
}

android {
    namespace = "com.andannn.melodify.ui.routes.library"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared:compose:components:library-item"))
            implementation(project(":shared:compose:components:library-detail"))
            implementation(project(":mobile-ui:common"))
        }
    }
}
