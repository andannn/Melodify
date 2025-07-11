package com.andanana.melodify.util

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

@ExperimentalKotlinGradlePluginApi
fun Project.configureKotlinMultiplatform(extension: KotlinMultiplatformExtension) {
    with(extension) {
        compilerOptions {
            // https://kotlinlang.org/docs/whatsnew22.html#preview-of-context-parameters
            freeCompilerArgs.add("-Xcontext-parameters")

            androidTarget {
                compilerOptions.jvmTarget.set(JvmTarget.JVM_17)

                // this is experimental API and will likely change in the future into more robust DSL
                @OptIn(ExperimentalKotlinGradlePluginApi::class)
                instrumentedTestVariant {
                    // !!! this makes instrumented tests depends on commonTest source set.
                    sourceSetTree.set(KotlinSourceSetTree.test)
                }
            }

            // share code in android and desktop
            applyDefaultHierarchyTemplate {
                common {
                    group("jvm") {
                        withJvm()
                        withAndroidTarget()
                    }
                }
            }
        }

        jvm("desktop")

        listOf(
            iosArm64(),
            iosSimulatorArm64(),
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "ComposeApp"
                isStatic = true
            }
        }

        sourceSets.apply {
            commonMain.dependencies {
                val bom = libs.findLibrary("koin-bom").get()
                implementation(project.dependencies.platform(bom))
                implementation(libs.findLibrary("koin.core").get())

                implementation(libs.findLibrary("kotlinx.collections.immutable").get())
                implementation(libs.findLibrary("kotlinx.coroutines.core").get())
                implementation(libs.findLibrary("napier").get())
                implementation(libs.findLibrary("kotlinx.datetime").get())
            }

            androidMain.dependencies {
                implementation(libs.findLibrary("koin.android").get())
            }

            commonTest.dependencies {
                implementation(libs.findLibrary("kotlin.test").get())
                implementation(libs.findLibrary("kotlinx.coroutines.test").get())
            }
        }
    }
}
