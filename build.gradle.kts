plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.devtoolsKsp) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

buildscript {
    dependencies {
        classpath (libs.androidx.navigation.safe.args.gradle.plugin)
        classpath (libs.com.google.devtools.ksp.gradle.plugin)
    }
}