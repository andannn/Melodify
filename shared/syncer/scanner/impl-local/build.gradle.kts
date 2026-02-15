

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroidTarget()
    withDesktopTarget()
    withIOSTarget()
}

kotlin {
    android {
        namespace = "com.andannn.melodify.core.syncer.scanner.impl.local"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:syncer:scanner:api"))
        }

        if (project.isConfigIOS()) {
            iosMain.dependencies {
                implementation(project(":shared:syncer:scanner:platform-ios"))
            }
        }
        jvmMain.dependencies {
            // https://mvnrepository.com/artifact/org/jaudiotagger
            implementation(libs.jaudiotagger)
            implementation(project(":shared:datastore"))
            implementation(project(":shared:database"))
        }
    }
}
