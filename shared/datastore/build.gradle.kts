plugins {
    id("melodify.kmp.library")
}

android {
    namespace = "com.andannn.melodify.core.datastore"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:platform"))

            implementation(libs.datastore)
            implementation(libs.okio)
            implementation(libs.datastore.preferences)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.okio)
        }
    }
}
