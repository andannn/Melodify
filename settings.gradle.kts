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
// include(":android-benchmark")

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

include(":shared:compose:popup:dialog:common")
include(":shared:compose:popup:dialog:entry:option")
include(":shared:compose:popup:dialog:entry:sort-rule")
include(":shared:compose:popup:dialog:entry:alert")
include(":shared:compose:popup:dialog:entry:sleep-timer")
include(":shared:compose:popup:dialog:entry:sync")
include(":shared:compose:popup:dialog:entry:play-list")

include(":shared:compose:popup:snack-bar")

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

include(":shared:domain:api")
include(":shared:domain:shared")
include(":shared:domain:impl")
include(":shared:domain:impl-player-none-android")
include(":shared:domain:impl-player-android")

include(":shared:player:common")
include(":shared:player:sleep-timer")
include(":shared:player:platform-player-ios")
include(":shared:player:impl-none-android")
include(":shared:player:impl-android")

include(":shared:datastore")
include(":shared:network")
include(":shared:database")

include(":shared:syncer:api")
include(":shared:syncer:impl")
include(":shared:syncer:scanner")
include(":shared:syncer:model")
include(":shared:syncer:platform-ios")

include(":shared:platform")
include(":shared:util:orientation")
include(":shared:util:immersive")
include(":shared:util:artwork-ios")
include(":shared:util:brightness")
include(":shared:util:volume")
