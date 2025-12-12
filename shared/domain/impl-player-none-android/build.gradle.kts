plugins {
    id("melodify.kmp.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify.domain.impl.none.android"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:domain:api"))
            implementation(project(":shared:database"))
            implementation(project(":shared:domain:shared"))
            implementation(project(":shared:player:impl-none-android"))
        }
    }
}
