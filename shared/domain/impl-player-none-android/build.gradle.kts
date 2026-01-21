plugins {
    id("kmp.ext")
}

kmpExt {
    withDesktopTarget()
    withIOSTarget()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:domain:api"))
            implementation(project(":shared:database"))
            implementation(project(":shared:domain:shared"))
            implementation(project(":shared:player:impl-none-android"))
        }
    }
}
