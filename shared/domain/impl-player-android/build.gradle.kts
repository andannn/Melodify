plugins {
    id("melodify.kmp.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify.domain.impl.android"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared:domain:shared"))
            implementation(project(":shared:domain:api"))
            implementation(project(":shared:player:impl-android"))
        }
    }
}
