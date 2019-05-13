# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Kshitij\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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

-dontwarn org.apache.**

-keep class org.apache.http.**

-keep interface org.apache.http.**

-dontwarn com.squareup.okhttp.**
-dontwarn java.awt.**
-dontwarn java.beans.Beans
-dontwarn javax.security.**
-keep class javamail.** {*;}
-keep class javax.mail.** {*;}
-keep class javax.activation.** {*;}
-keep class com.sun.mail.dsn.** {*;}
-keep class com.sun.mail.handlers.** {*;}
-keep class com.sun.mail.smtp.** {*;}
-keep class com.sun.mail.util.** {*;}
-keep class mailcap.** {*;}
-keep class mimetypes.** {*;}
-keep class myjava.awt.datatransfer.** {*;}
-keep class org.apache.harmony.awt.** {*;}
-keep class org.apache.harmony.misc.** {*;}
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

-keepclassmembers class * {
    private <fields>;
}

-keepclasseswithmembers class * { @com.activeandroid.annotation.Column <fields>; }
-keepattributes InnerClasses
-keep class com.beingdev.magicprint.prodcutscategory.Bags$* { *; }
-keep class com.beingdev.magicprint.prodcutscategory.Calendars$* { *; }
-keep class com.beingdev.magicprint.prodcutscategory.Cards$* { *; }
-keep class com.beingdev.magicprint.prodcutscategory.Keychains$* { *; }
-keep class com.beingdev.magicprint.prodcutscategory.Stationary$* { *; }
-keep class com.beingdev.magicprint.prodcutscategory.Tshirts$* { *; }

-keep class com.beingdev.magicprint.prodcutscategory.Bags** { *; }
-keep class com.beingdev.magicprint.prodcutscategory.Calendars** { *; }
-keep class com.beingdev.magicprint.prodcutscategory.Cards** { *; }
-keep class com.beingdev.magicprint.prodcutscategory.Keychains** { *; }
-keep class com.beingdev.magicprint.prodcutscategory.Stationary** { *; }
-keep class com.beingdev.magicprint.prodcutscategory.Tshirts** { *; }