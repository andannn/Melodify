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
include(":feature:common")
include(":feature:home")
include(":feature:player")
include(":feature:playlist")
include(":feature:customtab")
include(":feature:drawer")
include(":feature:message")
include(":core:syncer")
include(":core:platform")
