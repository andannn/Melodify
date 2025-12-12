plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
}

android {
    namespace = "com.andannn.melodify.shared.compose.components.search"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:compose:usecase"))
            implementation(project(":shared:compose:components:library-item"))
            implementation(project(":shared:domain:api"))
        }
    }
}
