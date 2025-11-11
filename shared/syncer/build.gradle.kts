plugins {
    id("melodify.kmp.library")
}

android {
    namespace = "com.andannn.melodify.core.syncer"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
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

        desktopMain.dependencies {
            // https://mvnrepository.com/artifact/org/jaudiotagger
            implementation(libs.jaudiotagger)
            implementation(project(":shared:datastore"))
        }

        androidMain.dependencies {
            implementation(libs.androidx.work.runtime.ktx)
        }
    }
}
