plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
}

android {
    namespace = "com.andannn.melodify.shared.compose.popup"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:common"))
            implementation(project(":shared:data"))

            implementation(libs.coil3.compose)
        }
    }
}
