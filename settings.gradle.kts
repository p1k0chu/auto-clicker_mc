pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version providers.gradleProperty("kotlin_version")
        id("net.fabricmc.fabric-loom") version providers.gradleProperty("loom_version")
    }
}

rootProject.name = "auto-clicker"
