plugins {
    id("kmp.ext")
}

kmpExt {
    withIOSTarget()
}

kotlin {
    sourceSets {
        iosMain.dependencies {
            api(project(":shared:syncer:scanner:common"))
        }
    }
}
