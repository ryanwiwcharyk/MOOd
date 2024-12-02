// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra.apply {
        set("room_version", "2.6.0")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    id("com.android.library") version "8.1.4" apply false
    alias(libs.plugins.kotlin.android) apply false
    //alias(libs.plugins.compose.compiler) apply false

}