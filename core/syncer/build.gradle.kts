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
        }

        desktopMain.dependencies {
            // https://mvnrepository.com/artifact/org/jaudiotagger
            implementation(libs.jaudiotagger)
            implementation(project(":core:datastore"))
        }
    }
}
