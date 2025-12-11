plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
}

android {
    namespace = "com.andannn.melodify.shared.compose.common"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:compose:resource"))
            api(project(":shared:data"))

            api(libs.retainedmodel)
            implementation(libs.coil3.compose)
            implementation(libs.reorderable)
        }
    }
}
