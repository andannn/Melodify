plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify.ui"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:compose:common"))
            api(project(":shared:compose:usecase"))
            api(project(":shared:compose:popup"))
            api(project(":shared:util:orientation"))
            api(project(":shared:util:immersive"))
        }
    }
}
