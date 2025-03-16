plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:data"))
            api(project(":core:platform"))
            implementation(project(":ui:common"))

            implementation(libs.coil3.compose)
            implementation(libs.reorderable)
        }

        androidMain.dependencies {
            implementation(libs.androidx.glance.appwidget)
            implementation(libs.androidx.glance.material3)
            implementation(libs.coil3.compose)
        }
    }
}

android {
    namespace = "com.andannn.melodify.ui.components"
}
