import com.andanana.melodify.util.libs
import org.gradle.api.Project

fun Project.isConfigIOS() = findProperty("kmp.configiOS") == "true"

val Project.compileSdkVersion
    get() = libs.findVersion("android-compileSdk").get().toString().toInt()
val Project.targetSdkVersion
    get() = libs.findVersion("android-targetSdk").get().toString().toInt()
val Project.minSdkVersion
    get() = libs.findVersion("android-minSdk").get().toString().toInt()
