plugins {
    id("melodify.kmp.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify.domain"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.paging.common)
        }
    }
}
