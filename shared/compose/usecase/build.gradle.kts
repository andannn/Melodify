plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
}

android {
    namespace = "com.andannn.melodify.shared.compose.usecase"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:domain:api"))
            api(project(":shared:compose:common"))
            api(project(":shared:compose:popup"))
        }
    }
}
