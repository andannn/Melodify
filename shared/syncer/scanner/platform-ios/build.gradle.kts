plugins {
    id("kmp.ext")
}

kmpExt {
    withIOS()
}

kotlin {
    sourceSets {
        iosMain.dependencies {
            api(project(":shared:syncer:scanner:common"))
        }
    }
}
