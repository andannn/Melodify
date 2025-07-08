import java.net.URI

dependencyResolutionManagement {
    repositories {
        maven {
            url = URI("https://plugins.gradle.org/m2/")
        }
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"

include(":android-plugins")
