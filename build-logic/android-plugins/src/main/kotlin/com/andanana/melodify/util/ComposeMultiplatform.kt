package com.andanana.melodify.util

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@ExperimentalKotlinGradlePluginApi
fun Project.configureComposeMultiplatform(extension: KotlinMultiplatformExtension) {
    with(extension) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.plugin.compose")
            apply("org.jetbrains.compose")
        }
        val compose: ComposePlugin.Dependencies = dependencies.extensions.getByType(ComposePlugin.Dependencies::class.java)

        sourceSets.apply {
            commonMain.dependencies {
                implementation(libs.findLibrary("jetbrains.compose.material3").get())
                implementation(libs.findLibrary("jetbrains.compose.animation").get())
                implementation(libs.findLibrary("jetbrains.compose.resources").get())
                implementation(libs.findLibrary("jetbrains.compose.foundation").get())
                implementation(libs.findLibrary("jetbrains.compose.runtime").get())
                implementation(libs.findLibrary("jetbrains.compose.ui").get())
                implementation(libs.findLibrary("jetbrains.compose.ui.util").get())
                implementation(libs.findLibrary("jetbrains.compose.ui.tooling").get())
                implementation(libs.findLibrary("jetbrains.compose.ui.tooling.preview").get())
                implementation(libs.findLibrary("jetbrains.compose.ui.backhandler").get())
                implementation(libs.findLibrary("jetbrains.material.icons.extended").get())
                implementation(libs.findLibrary("lifecycle.runtime.compose").get())
            }

            commonTest.dependencies {
                implementation(libs.findLibrary("jetbrains.compose.ui.test").get())
            }

            androidMain.dependencies {
                implementation(libs.findLibrary("androidx.navigation3.ui").get())
            }

            androidInstrumentedTest.dependencies {
                implementation(libs.findLibrary("ui.test.manifest").get())
                implementation(libs.findLibrary("ui.test.junit4.android").get())
            }

            val desktopMain = getByName("desktopMain")
            desktopMain.dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.findLibrary("jetbrains.compose.desktop").get())
            }

            val desktopTest = getByName("desktopTest")
            desktopTest.dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

@ExperimentalKotlinGradlePluginApi
fun Project.configureComposeBuildFeature(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    with(commonExtension) {
        dependencies {
            buildFeatures {
                compose = true
            }
        }
    }
}
