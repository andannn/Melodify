plugins {
    id("melodify.kmp.library")
}

android {
    namespace = "com.andannn.melodify.core.syncer"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:database"))
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        desktopMain.dependencies {
            // https://mvnrepository.com/artifact/org/jaudiotagger
            implementation(libs.jaudiotagger)
            implementation(project(":core:datastore"))
        }
    }
}
