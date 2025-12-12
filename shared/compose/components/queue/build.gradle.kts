plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
}

android {
    namespace = "com.andannn.melodify.shared.compose.components.queue"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:usecase"))
            implementation(project(":shared:domain:api"))

            implementation(libs.reorderable)
        }
    }
}
