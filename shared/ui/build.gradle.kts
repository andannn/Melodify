plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify.shared.ui"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:data"))
            implementation(project(":shared:syncer"))
            implementation(project(":shared:platform"))

            implementation(libs.coil3.compose)
            implementation(libs.reorderable)
            implementation(libs.androidx.paging.compose)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = auto
}
