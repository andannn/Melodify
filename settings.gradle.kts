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
include(":desktopApp")
include(":androidApp")
include(":shared:ui")
include(":shared:data")
include(":shared:player")
include(":shared:datastore")
include(":shared:network")
include(":shared:database")
include(":shared:syncer")
include(":shared:platform")
