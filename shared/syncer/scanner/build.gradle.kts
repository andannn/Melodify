plugins {
    alias(libs.plugins.android.library)
    id("kmp.ext")
}

android {
    namespace = "com.andannn.melodify.core.syncer.scanner"
}

kmpExt {
    withAndroid()
    withDesktop()
    withIOS()
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:syncer:model"))
        }
        iosMain.dependencies {
            implementation(project(":shared:syncer:platform-ios"))
        }
        getByName("desktopMain").dependencies {
            // https://mvnrepository.com/artifact/org/jaudiotagger
            implementation(libs.jaudiotagger)
            implementation(project(":shared:datastore"))
            implementation(project(":shared:database"))
        }
    }
}
