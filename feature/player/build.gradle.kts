plugins {
    id("melodify.kmp.library")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.ui.tooling)
        }

        commonMain.dependencies {
            implementation(project(":feature:common"))
            implementation(project(":core:data"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.animation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.coil3.compose)

            implementation(libs.koin.core.viewmodel)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.navigation.compose)

            implementation(libs.reorderable)
        }
    }
}

android {
    namespace = "com.andannn.melodify.feature.player"

    buildFeatures {
        compose = true
    }
}
dependencies {
    debugImplementation(libs.androidx.ui.tooling)
}