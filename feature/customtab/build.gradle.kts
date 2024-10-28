plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:common"))
            implementation(project(":core:data"))

            implementation(libs.koin.compose.viewmodel)
            implementation(libs.navigation.compose)
            implementation(libs.reorderable)
        }
    }
}

android {
    namespace = "com.andannn.melodify.feature.customtab"
}