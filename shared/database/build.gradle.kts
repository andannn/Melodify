import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

kmpExt {
    withAndroid()
    withDesktop()
    withIOS()
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.ui.database"
    }
// TODO:
//    androidTarget {
//        // this is experimental API and will likely change in the future into more robust DSL
//        @OptIn(ExperimentalKotlinGradlePluginApi::class)
//        instrumentedTestVariant {
//            // !!! this makes instrumented tests depends on commonTest source set.
//            sourceSetTree.set(KotlinSourceSetTree.test)
//        }
//    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:platform"))
            implementation(libs.room.runtime)
            implementation(libs.okio)
            implementation(libs.room.paging)
        }

        iosMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
        }

        getByName("androidDeviceTest").dependencies {

            implementation(libs.room.runtime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.androidx.room.testing)
            implementation(libs.okio)
        }

        getByName("desktopMain").dependencies {
            implementation(libs.androidx.sqlite.bundled)
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspDesktop", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}
