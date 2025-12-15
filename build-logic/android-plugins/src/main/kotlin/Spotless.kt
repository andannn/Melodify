import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Project

fun SpotlessExtension.configureSpotless(project: Project) {
    kotlin {
        target("src/**/*.kt")
        licenseHeaderFile(project.rootProject.file("spotless/copyright.txt"))
    }
}
