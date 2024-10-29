import com.andanana.melodify.util.configureComposeBuildFeature
import com.andanana.melodify.util.configureComposeMultiplatform
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeMultiplatformLibraryConventionPlugin : Plugin<Project> {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<KotlinMultiplatformExtension> {
                configureComposeMultiplatform(this)
            }

            extensions.configure<LibraryExtension> {
                configureComposeBuildFeature(this)
            }
        }
    }
}