plugins {
    id("melodify.kmp.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify.domain.impl.shared"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:domain:api"))
            implementation(project(":shared:database"))
            implementation(project(":shared:network"))
        }
    }
}
