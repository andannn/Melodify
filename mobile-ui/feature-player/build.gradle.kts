plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify.ui.player"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared:ui"))
            implementation(project(":shared:data"))
            implementation(project(":shared:platform"))
            implementation(project(":mobile-ui:common"))
            implementation(libs.coil3.compose)
            implementation(libs.androidx.palette)
            implementation(libs.material.color.utilities.android)
        }
    }
}
