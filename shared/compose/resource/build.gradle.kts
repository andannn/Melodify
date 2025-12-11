plugins {
    id("melodify.kmp.library")
    id("melodify.compose.multiplatform.library")
}

android {
    namespace = "com.andannn.melodify.shared.compose.resource"
}
compose.resources {
    publicResClass = true
    generateResClass = auto
}
