plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
}

android {
    namespace = "com.andannn.melodify.ui.player"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:common"))
            implementation(project(":shared:platform"))
            implementation(project(":shared:compose:components:lyrics"))
            implementation(project(":shared:compose:components:queue"))
            implementation(project(":shared:compose:components:play-control"))
            implementation(project(":mobile-ui:common"))

            implementation(libs.coil3.compose)
        }

        androidMain.dependencies {
            implementation(project(":shared:player:impl-android"))
            implementation(libs.androidx.media3.ui.compose)
            implementation(libs.androidx.palette)
            implementation(libs.material.color.utilities.android)
        }
    }
}
