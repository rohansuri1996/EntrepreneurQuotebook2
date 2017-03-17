# EntrepreneurQuotebook2

Avi's Branch

Doesn't have login activity
Has fixed share button
Other small fixes
Build.gradle with latest tools
Only saves english language to save space
Working on proguard (minimizes code)
makes it harder for people to steal



Add these to proguard:

-keep class com.squareup.okhttp.** { *; }

-keep interface com.squareup.okhttp.** { *; }
Complete proguard-rules.pro file will look like:

-dontwarn rx.**

-dontwarn okio.**

-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-dontwarn retrofit.**
-dontwarn retrofit.appengine.UrlFetchClient
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keepattributes Signature
-keepattributes *Annotation*

http://www.kevinrschultz.com/blog/2014/02/15/proguard-with-gradle/
https://github.com/krschultz/android-proguard-snippets
