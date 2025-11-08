# proguard-rules.pro
-dontoptimize
-dontobfuscate
-dontwarn kotlinx.**
-dontwarn okhttp3.internal.platform.**
-dontwarn com.sun.jna.internal.**
-dontwarn org.graalvm.nativeimage.**
-dontwarn com.oracle.svm.**
-dontwarn okhttp3.internal.graal.**

-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }
-keep class uk.co.caprica.vlcj.** { *; }

# Keep the MelodifyDataBase_Impl class (usually generated for Room)
-keep class com.andannn.melodify.core.database.MelodifyDataBase_Impl {
    *;
}
-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class io.ktor.** { *; }

-keep class ** extends com.sun.jna.Structure { *; }
-keep class ** extends com.sun.jna.Library { *; }
-keep class com.sun.jna.** { *; }
-keep class org.jaudiotagger.tag.reference.Tagger { *; }
-keep class org.jaudiotagger.tag.reference.Tagger { *; }
-keep class com.andannn.melodify.core.player.LocalDiscoveryDirectoryProvider { *; }

-keep class androidx.sqlite.driver.bundled.** { *; }
-dontwarn androidx.sqlite.driver.bundled.**
