pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://packages.jetbrains.team/maven/p/amper/amper")
        maven("https://www.jetbrains.com/intellij-repository/releases")
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    }
}

plugins {
    id("org.jetbrains.amper.settings.plugin").version("0.5.0-dev-940")
}


include("ksoup", "ksoup-engine-common")
include("ksoup-engine-korlibs")
include("ksoup-network-korlibs")
include("ksoup-test")
include("ksoup-engine-kotlinx")
//include("sample:shared", "sample:android", "sample:desktop", "sample:ios")