import org.gradle.api.Project

fun Project.isConfigIOS() = findProperty("kmp.configiOS") == "true"
