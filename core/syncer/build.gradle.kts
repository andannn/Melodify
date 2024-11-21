plugins {
    id("melodify.kmp.library")
}

android {
    namespace = "com.andannn.melodify.core.syncer"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:library"))
        }
    }
}
