plugins {
    id("melodify.kmp.library")
    id("com.google.devtools.ksp")
    alias(libs.plugins.room)
}

android {
    namespace = "com.andannn.melodify.feature.database"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

room {
    schemaDirectory("$projectDir/schemas")
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

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.test.runner)
            implementation(libs.androidx.test.core.ktx)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.androidx.room.testing)
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}