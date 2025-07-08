import com.andanana.melodify.util.configureKotlinAndroid
import com.andanana.melodify.util.configureKotlinMultiplatform
import com.andanana.melodify.util.configureKtLint
import com.andanana.melodify.util.configureLicense
import com.andanana.melodify.util.configureSpotless
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

class KMPApplicationConventionPlugin : Plugin<Project> {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.parcelize")
                apply("org.jlleitschuh.gradle.ktlint")
            }

            configureSpotless()
            configureLicense()

            extensions.configure<KotlinMultiplatformExtension> {
                configureKotlinMultiplatform(this)
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 35
            }

            extensions.configure<KtlintExtension> {
                configureKtLint(this)
            }
        }
    }
}