plugins {
    id("melodify.kmp.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.andannn.melodify.core.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:datastore"))
            implementation(project(":shared:network"))
            implementation(project(":shared:database"))
            implementation(project(":shared:player"))
            implementation(project(":shared:platform"))
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.paging.common)
        }
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.guava)

    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.session)
}
