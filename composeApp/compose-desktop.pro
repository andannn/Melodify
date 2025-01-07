# proguard-rules.pro
-dontoptimize
-dontobfuscate
-dontwarn kotlinx.**
-dontwarn okhttp3.internal.platform.**

-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }
-keep class uk.co.caprica.vlcj.** { *; }
