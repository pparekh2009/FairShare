// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false

//    id 'com.google.gms:google-services:4.3.5' apply false
//    id 'com.google.gms.google-services' version '4.3.5' apply false
//    alias(libs.plugins.compose.compiler) apply false
}