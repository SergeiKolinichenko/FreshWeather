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

# -------------------------------------------------------------------

# The -optimizations option disables some arithmetic simplifications that Dalvik 1.0 and 1.5 can't handle.
# Note that the Dalvik VM also can't handle aggressive overloading (of static fields).
# To understand or change this check http://proguard.sourceforge.net/index.html#/manual/optimizations.html
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Preverification is irrelevant for the dex compiler and the Dalvik VM.
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*

# The solution to the problem with annotations @SerializedName of Retrofit
 # was to add the following rule to the proguard configuration file:
-dontwarn java.lang.invoke.StringConcatFactory
-keep class * { @com.google.gson.annotations.SerializedName <fields>; }
