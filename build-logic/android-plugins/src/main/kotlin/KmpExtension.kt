import com.andanana.melodify.util.libs
import com.android.build.api.dsl.androidLibrary
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import javax.inject.Inject

abstract class KmpExtension
    @Inject
    constructor(
        val project: Project,
    ) {
        private val libs get() = project.libs
        private var alreadyConfigCommonDependency = false

        private var composeEnabled: Boolean = false

        private var isAndroidConfig: Boolean = false
        private var isDesktopConfig: Boolean = false

        init {
            composeEnabled = project.pluginManager.hasPlugin("org.jetbrains.kotlin.plugin.compose")
        }

        fun withDesktop() {
            isDesktopConfig = true

            project.extensions.configure<KotlinMultiplatformExtension> {
                jvm("desktop")
                configCommonDependencyIfNeeded()

                if (composeEnabled) {
                    sourceSets.apply {
                        val desktopMain = getByName("desktopMain")
                        desktopMain.dependencies {
                            implementation(libs.findLibrary("jetbrains.compose.desktop").get())
                        }

                        val desktopTest = getByName("desktopTest")
                        desktopTest.dependencies {}
                    }
                }

                addJvmTargetIfNeeded()
            }
        }

        fun withAndroid(config: AndroidConfigParam.() -> Unit = {}) {
            val config = AndroidConfigParam().apply(config)

            isAndroidConfig = true

            // AGP config
            project.pluginManager.apply("com.android.kotlin.multiplatform.library")

            project.extensions.configure<KotlinMultiplatformExtension> {
                androidLibrary {
                    compileSdk = 36
                    minSdk = 30

                    if (config.enableHostTest) {
                        withHostTestBuilder {
                            sourceSetTreeName = "test".takeIf { config.includeHostTestToCommonTest }
                        }.configure {
                            isIncludeAndroidResources = true
                        }
                    }

                    if (config.enableDeviceTest) {
                        withDeviceTestBuilder {
                            sourceSetTreeName = "test".takeIf { config.includeDeviceTestToCommonTest }
                        }.configure {
                            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                        }
                    }

                    compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
                }

                addJvmTargetIfNeeded()

                configCommonDependencyIfNeeded()

                sourceSets.apply {
                    androidMain.dependencies {
                        implementation(libs.findLibrary("koin.android").get())
                    }

                    if (config.enableHostTest) {
                        getByName("androidHostTest").dependencies {}
                    }

                    if (config.enableDeviceTest) {
                        getByName("androidDeviceTest").dependencies {
                            implementation(libs.findLibrary("koin.test.junit4").get())
                            implementation(libs.findLibrary("androidx.test.runner").get())
                            implementation(libs.findLibrary("androidx.test.core.ktx").get())
                            implementation(libs.findLibrary("androidx.test.ext.junit").get())

                            if (composeEnabled) {
                                implementation(libs.findLibrary("compose.ui.test.manifest").get())
                                implementation(libs.findLibrary("compose.ui.test.junit4.android").get())
                            }
                        }
                    }
                }
            }
        }

        fun withIOS() {
            project.extensions.configure<KotlinMultiplatformExtension> {
                listOf(
                    iosArm64(),
                    iosSimulatorArm64(),
                )
                configCommonDependencyIfNeeded()
            }
        }

        private fun KotlinMultiplatformExtension.configCommonDependencyIfNeeded() {
            if (!alreadyConfigCommonDependency) {
                configKMPCommonDependency()

                if (composeEnabled) {
                    configCMPCommonDependency()
                }

                alreadyConfigCommonDependency = true
            }
        }

        private fun KotlinMultiplatformExtension.configCMPCommonDependency() {
            sourceSets.apply {
                commonMain.dependencies {
                    implementation(libs.findLibrary("jetbrains.compose.material3").get())
                    implementation(libs.findLibrary("jetbrains.compose.animation").get())
                    implementation(libs.findLibrary("jetbrains.compose.resources").get())
                    implementation(libs.findLibrary("jetbrains.compose.foundation").get())
                    implementation(libs.findLibrary("jetbrains.compose.runtime").get())
                    implementation(libs.findLibrary("jetbrains.compose.ui").get())
                    implementation(libs.findLibrary("jetbrains.compose.ui.util").get())
                    implementation(libs.findLibrary("jetbrains.compose.ui.tooling.preview").get())
                    implementation(libs.findLibrary("navigationevent.compose").get())
                    implementation(libs.findLibrary("jetbrains.material.icons.extended").get())
                    implementation(libs.findLibrary("lifecycle.runtime.compose").get())
                    implementation(libs.findLibrary("navigation3.runtime").get())
                }

                commonTest.dependencies {
                    implementation(libs.findLibrary("jetbrains.compose.ui.test").get())
                }
            }
        }

        private fun KotlinMultiplatformExtension.configKMPCommonDependency() {
            sourceSets.apply {
                commonMain.dependencies {
                    val bom = libs.findLibrary("koin-bom").get()
                    implementation(project.dependencies.platform(bom))
                    implementation(libs.findLibrary("koin.core").get())

                    implementation(libs.findLibrary("kotlinx.collections.immutable").get())
                    implementation(libs.findLibrary("kotlinx.coroutines.core").get())
                    implementation(libs.findLibrary("napier").get())
                }

                commonTest.dependencies {
                    implementation(libs.findLibrary("kotlin.test").get())
                    implementation(libs.findLibrary("kotlinx.coroutines.test").get())
                    implementation(libs.findLibrary("turbine").get())
                }
            }
        }

        private fun KotlinMultiplatformExtension.addJvmTargetIfNeeded() {
            if (isDesktopConfig && isAndroidConfig) {
                configJvmTarget()
            }
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        private fun KotlinMultiplatformExtension.configJvmTarget() {
//
// TODO: desktop and android Common source set can not be applied after migrate to Kmp agp plugin(https://developer.android.com/kotlin/multiplatform/plugin#features).
//  probably because androidTarget() is deprecated.
//  Uncomment this setting in the future
//          applyDefaultHierarchyTemplate {
//                common {
//                    group("deskTopAndAndroid") {
//                        withJvm()
//                        withAndroidTarget()
//                    }
//                }
//            }
        }
    }

class AndroidConfigParam(
    var enableHostTest: Boolean = true,
    var includeHostTestToCommonTest: Boolean = true,
    var enableDeviceTest: Boolean = false,
    var includeDeviceTestToCommonTest: Boolean = false,
)
