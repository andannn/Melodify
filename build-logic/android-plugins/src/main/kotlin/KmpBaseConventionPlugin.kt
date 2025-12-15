import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

class KmpBaseConventionPlugin : Plugin<Project> {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    override fun apply(project: Project) =
        with(project) {
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            extensions.configure<KotlinMultiplatformExtension> {
                compilerOptions {
                    // https://kotlinlang.org/docs/whatsnew22.html#preview-of-context-parameters
                    freeCompilerArgs.add("-Xcontext-parameters")
                }
            }

            extensions.create<KmpExtension>("kmpExt")

            pluginManager.apply("org.jlleitschuh.gradle.ktlint")
            extensions.configure<KtlintExtension> {
                configureKtLint()
            }

            pluginManager.apply("com.diffplug.spotless")
            extensions.configure<SpotlessExtension> {
                configureSpotless(project)
            }
        }
}
