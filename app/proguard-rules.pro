# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Firebase Firestore models
-keepclassmembers class com.google.firebase.firestore.** { *; }
-keepattributes *Annotation*
-keepclassmembers class com.google.firestore.v1.** { *; }
-keepnames class com.google.firebase.** { *; }
-keepnames class com.google.firestore.v1.** { *; }

# Kotlin Coroutines (if you're using coroutines)
-keepclassmembers class kotlinx.coroutines.** { *; }

# Keep Serializable Data Classes (like your Opening class)
-keepclassmembers class com.example.crackincompose.** { *; }

# Keep serialization-related classes
-keep class kotlinx.serialization.** { *; }
-keep class com.example.upaayforastrologers.** { *; }

-keep class * {
    @kotlinx.serialization.Serializable <fields>;
}

