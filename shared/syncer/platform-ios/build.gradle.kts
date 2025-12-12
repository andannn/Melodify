plugins {
    id("melodify.kmp.library")
}

android {
    namespace = "com.andannn.melodify.core.syncer.platform.ios"
}

kotlin {
    sourceSets {
        iosMain.dependencies {
            api(project(":shared:syncer:model"))
        }
    }
}
