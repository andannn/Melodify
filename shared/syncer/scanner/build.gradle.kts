plugins {
    id("melodify.kmp.library")
}

android {
    namespace = "com.andannn.melodify.core.syncer.scanner"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:syncer:model"))
        }
        iosMain.dependencies {
            implementation(project(":shared:syncer:platform-ios"))
        }
        desktopMain.dependencies {
            // https://mvnrepository.com/artifact/org/jaudiotagger
            implementation(libs.jaudiotagger)
            implementation(project(":shared:datastore"))
            implementation(project(":shared:database"))
        }
    }
}
