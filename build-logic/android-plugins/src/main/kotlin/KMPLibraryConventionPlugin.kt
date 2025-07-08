import com.andanana.melodify.util.configureKotlinAndroid
import com.andanana.melodify.util.configureKotlinMultiplatform
import com.andanana.melodify.util.configureKtLint
import com.andanana.melodify.util.configureSpotless
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

class KMPLibraryConventionPlugin : Plugin<Project> {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.parcelize")
                apply("org.jlleitschuh.gradle.ktlint")
            }

            configureSpotless()

            extensions.configure<KotlinMultiplatformExtension> {
                configureKotlinMultiplatform(this)
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
            }

            extensions.configure<KtlintExtension> {
                configureKtLint(this)
            }
        }
    }
}
