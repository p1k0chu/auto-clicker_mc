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
        id("net.fabricmc.fabric-loom-remap") version providers.gradleProperty("loom_version")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version providers.gradleProperty("stonecutter_version")
}

stonecutter {
    create(rootProject) {
        versions("1.21.11", "26.1.2")
        vcsVersion = "26.1.2"
    }
}

rootProject.name = "auto-clicker"
