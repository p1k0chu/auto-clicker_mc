import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.21"
    id("fabric-loom") version "1.9.1"
    id("com.modrinth.minotaur") version "2.+"
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String
val mod_version: String by project
val minecraft_version: String by project
val loader_version: String by project
val kotlin_loader_version: String by project

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("auto_clicker") {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", minecraft_version)
    inputs.property("loader_version", loader_version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to minecraft_version,
            "loader_version" to loader_version,
            "kotlin_loader_version" to kotlin_loader_version
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = "JAYD9vCA" // This can be the project ID or the slug. Either will work!
    syncBodyFrom = rootProject.file("README.md").readText()

    versionNumber = mod_version
    versionType = "release" // `release`, `beta` or `alpha`
    gameVersions.add(minecraft_version)

    uploadFile.set(tasks.remapJar)
    loaders.add("fabric")

    dependencies { // A special DSL for creating dependencies
        // scope.type
        // The scope can be `required`, `optional`, `incompatible`, or `embedded`
        // The type can either be `project` or `version`
        required.project("fabric-api") // Creates a new required dependency on Fabric API
        required.project("fabric-language-kotlin")
    }
}
