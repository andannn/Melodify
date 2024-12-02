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
include(":ui:common")
include(":ui:player")
include(":core:syncer")
include(":core:platform")
include(":ui:components")
