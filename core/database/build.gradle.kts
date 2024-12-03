plugins {
    id("melodify.kmp.library")
    id("com.google.devtools.ksp")
    alias(libs.plugins.room)
}

android {
    namespace = "com.andannn.melodify.ui.database"

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
            implementation(project(":core:platform"))
            implementation(libs.room.runtime)
            implementation(libs.okio)
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
            implementation(libs.okio)
        }

        desktopMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspDesktop", libs.room.compiler)
}