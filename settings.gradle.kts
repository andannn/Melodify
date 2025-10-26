pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Melodify"
include(":composeApp")
include(":core:data")
include(":core:player")
include(":core:datastore")
include(":core:network")
include(":core:database")
include(":core:syncer")
include(":core:platform")
