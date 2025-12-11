plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify.ui"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            api(project(":shared:compose:common"))
            api(project(":shared:compose:usecase"))
            api(project(":shared:compose:popup"))
        }
    }
}
