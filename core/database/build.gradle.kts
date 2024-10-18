plugins {
    id("melodify.kmp.library")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.andannn.melodify.feature.database"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.room.runtime)
        }

        iosMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.coil.network.ktor3)
            implementation(libs.ktor.client.darwin)
        }
    }
}

dependencies {
    ksp(libs.room.compiler)
}