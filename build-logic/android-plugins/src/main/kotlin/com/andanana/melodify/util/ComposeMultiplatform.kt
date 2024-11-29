package com.andanana.melodify.util

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@ExperimentalKotlinGradlePluginApi
fun Project.configureComposeMultiplatform(
    extension: KotlinMultiplatformExtension,
) {
    with(extension) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.plugin.compose")
            apply("org.jetbrains.compose")
        }
        val compose: ComposePlugin.Dependencies = dependencies.extensions.getByType(ComposePlugin.Dependencies::class.java)

        sourceSets.apply {
            androidMain.dependencies {
                implementation(compose.preview)
                implementation(libs.findLibrary("androidx.ui.tooling").get())
            }

            commonMain.dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.animation)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.uiToolingPreview)
            }

            val desktopMain = getByName("desktopMain")
            desktopMain.dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.desktop.common)
            }
        }
    }
}


@ExperimentalKotlinGradlePluginApi
fun Project.configureComposeBuildFeature(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    with(commonExtension) {
        dependencies {
            buildFeatures {
                compose = true
            }
        }
    }
}