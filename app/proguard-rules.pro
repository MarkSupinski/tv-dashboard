# Add project specific ProGuard rules here.
# Keep OkHttp's internal types (no R8 shrinking is enabled by default).
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
