plugins {
    id("kmp.ext")
}

kmpExt {
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":shared:player:common"))
        }
    }
}
