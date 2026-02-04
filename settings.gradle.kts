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
val configIOS: Boolean =
    providers.gradleProperty("kmp.configiOS").map { it == "true" }.getOrElse(true)
include(":android-app")
// include(":android-benchmark")

if (configIOS) include(":ios-app")

include(":desktop-app")

include(":mobile-ui:common")
include(":mobile-ui:app")
include(":mobile-ui:feature-player")
include(":mobile-ui:feature-home")
include(":mobile-ui:feature-library")
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
if (configIOS) include(":shared:player:platform-player-ios")
include(":shared:player:impl-none-android")
include(":shared:player:impl-android")

include(":shared:datastore")

include(":shared:database")

include(":shared:network:common")
include(":shared:network:service:lrclib")
include(":shared:network:service:siren")

include(":shared:syncer:api")
include(":shared:syncer:impl")
include(":shared:syncer:scanner:api")
include(":shared:syncer:scanner:common")
include(":shared:syncer:scanner:impl-local")
include(":shared:syncer:scanner:impl-monster-siren")
if (configIOS) include(":shared:syncer:scanner:platform-ios")

include(":shared:platform")

include(":shared:util:orientation")
include(":shared:util:immersive")
if (configIOS) include(":shared:util:artwork-ios")
include(":shared:util:brightness")
include(":shared:util:volume")
