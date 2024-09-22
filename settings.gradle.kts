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
include(":app")
include(":feature:home")
include(":feature:player")
include(":feature:playlist")
include(":core:designsystem")
include(":core:mediastore")
include(":core:data")
include(":core:player")
include(":core:datastore")
include(":core:domain")
include(":feature:common")
