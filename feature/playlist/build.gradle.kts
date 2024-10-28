plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:common"))
            implementation(project(":core:data"))

            implementation(libs.coil3.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.navigation.compose)
        }
    }
}

android {
    namespace = "com.andannn.melodify.feature.playlist"
}
