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

include(":android-app")
include(":android-benchmark")

include(":ios-app")

include(":desktop-app")

include(":mobile-ui:common")
include(":mobile-ui:app")
include(":mobile-ui:feature-player")
include(":mobile-ui:feature-home")
include(":mobile-ui:feature-library")
include(":mobile-ui:feature-search")
include(":mobile-ui:feature-tab-management")

include(":shared:compose:resource")
include(":shared:compose:common")
include(":shared:compose:popup")
include(":shared:compose:usecase")
include(":shared:compose:components:tab")
include(":shared:compose:components:lyrics")
include(":shared:compose:components:queue")
include(":shared:compose:components:search")
include(":shared:compose:components:library-item")
include(":shared:compose:components:library-detail")
include(":shared:compose:components:play-control")
include(":shared:compose:components:tab-content")
include(":shared:compose:components:tab-management")

include(":shared:data")

include(":shared:player")
include(":shared:datastore")
include(":shared:network")
include(":shared:database")
include(":shared:syncer")
include(":shared:platform")
include(":shared:util:orientation")
include(":shared:util:immersive")
