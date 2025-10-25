import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    id("melodify.kmp.library")
    id("com.google.devtools.ksp")
    alias(libs.plugins.room)
}

android {
    namespace = "com.andannn.melodify.ui.database"
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    androidTarget {
        // this is experimental API and will likely change in the future into more robust DSL
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            // !!! this makes instrumented tests depends on commonTest source set.
            sourceSetTree.set(KotlinSourceSetTree.test)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:platform"))
            implementation(libs.room.runtime)
            implementation(libs.okio)
            implementation(libs.room.paging)
        }

        iosMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.test.runner)
            implementation(libs.androidx.test.core.ktx)
            implementation(libs.room.runtime)
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
