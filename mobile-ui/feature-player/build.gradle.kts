plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withIOSTarget()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.ui.player"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:common"))
            implementation(project(":shared:platform"))
            implementation(project(":shared:util:brightness"))
            implementation(project(":shared:util:volume"))
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
