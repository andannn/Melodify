plugins {
    alias(libs.plugins.android.library)
    id("kmp.ext")
}

android {
    namespace = "com.andannn.melodify.core.syncer"
}

kmpExt {
    withAndroid()
    withDesktop()
    withIOS()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:syncer:scanner"))
            implementation(project(":shared:database"))
            implementation(project(":shared:datastore"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.test.runner)
            implementation(libs.androidx.test.core.ktx)
        }

        getByName("desktopMain").dependencies {
            // https://mvnrepository.com/artifact/org/jaudiotagger
            implementation(libs.jaudiotagger)
            implementation(project(":shared:datastore"))
        }

        androidMain.dependencies {
            implementation(libs.androidx.work.runtime.ktx)
        }
    }
}
