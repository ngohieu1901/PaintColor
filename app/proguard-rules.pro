# ---------- MẶC ĐỊNH CÓ -----------------

# support v4
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**


# google ads
-keep class com.google.android.gms.internal.** { *; }

# ---------- NẾU CALL API HOẶC DÙNG FIREBASE READTIME THÌ ADD CLASS VS MODEL SỬ DỤNG   -----------------

-keep class com.paintcolor.drawing.paint.presentations.feature.screen_base.splash.SplashActivity.** { *; }
-keep class com.paintcolor.drawing.paint.application.** { *; }
-keep class com.paintcolor.drawing.paint.domain.model.** { *; }

# ---------- TÙY TỪNG PROJECT CÓ DÙNG KO NẾU DÙNG THÌ ADD OR NHỮNG CLASS STATIC ----------

#custom views
-keep public class * extends android.view.View {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

# enum
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-dontnote java.nio.file.Files, java.nio.file.Path
-dontnote **.ILicensingService

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-keep class com.amazic.library.**{ *; }
-keep class com.amazic.mylibrary.**{ *; }
-keep class retrofit2.**{ *; }

 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-keep class com.paintcolor.drawing.paint.data.dto** { *; }
-keep class com.paintcolor.drawing.paint.domain.model.** { *; }