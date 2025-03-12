import com.andanana.melodify.util.configureKotlinAndroid
import com.andanana.melodify.util.configureKotlinMultiplatform
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KMPLibraryConventionPlugin : Plugin<Project> {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.parcelize")
            }

            extensions.configure<KotlinMultiplatformExtension> {
                configureKotlinMultiplatform(this)
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
            }

        }
    }
}