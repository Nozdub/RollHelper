# Preserve your app code and UI safely while enabling shrinking/obfuscation

# Keep all classes in your app package
-keep class com.chriaasen.rollhelper.** { *; }

# Preserve all Composable functions (important for Jetpack Compose)
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Do not warn about Compose internals (they’re safe to ignore)
-dontwarn androidx.compose.**

# Preserve metadata for annotations and Kotlin reflection (safe default)
-keepattributes *Annotation*

# If you use Gson or reflection-based serialization, keep model classes
# (uncomment if needed)
# -keepclassmembers class com.chriaasen.rollhelper.** {
#     <fields>;
# }

# Preserve DataStore and protobuf classes if needed
-keep class com.chriaasen.rollhelper.**.proto.** { *; }

# Optional: Keep line numbers for better crash reports (optional)
# -keepattributes SourceFile,LineNumberTable
