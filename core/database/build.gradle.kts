plugins {
    id("melodify.kmp.library")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.andannn.melodify.feature.database"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
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

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

dependencies {
    androidTestImplementation(libs.androidx.test.runner)
    implementation(libs.core.ktx)
    ksp(libs.room.compiler)
}