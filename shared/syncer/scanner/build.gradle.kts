

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withDesktop()
    withIOS()
}

kotlin {
    androidLibrary {
        namespace = "com.andannn.melodify.core.syncer.scanner"
    }

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
