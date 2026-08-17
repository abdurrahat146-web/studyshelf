# Keep Supabase/Ktor/Kotlinx serialization models
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.studyshelf.app.**$$serializer { *; }
-keepclassmembers class com.studyshelf.app.** { *** Companion; }
-keepclasseswithmembers class com.studyshelf.app.** { kotlinx.serialization.KSerializer serializer(...); }

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
