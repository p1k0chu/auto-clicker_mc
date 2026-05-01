import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("net.fabricmc.fabric-loom")
    id("com.modrinth.minotaur") version "2.+"
}

val targetJavaVersion = 25
java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
    targetCompatibility = JavaVersion.VERSION_25
    sourceCompatibility = JavaVersion.VERSION_25
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register(project.name) {
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
    implementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    implementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    implementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_version")}")
}

tasks.processResources {
    val version = project.version
    val mcVer = project.property("minecraft_version")!!
    val loaderVer = project.property("loader_version")!!
    val kotlinModVer = project.property("kotlin_loader_version")!!
    inputs.property("version", version)
    inputs.property("minecraft_version", mcVer)
    inputs.property("loader_version", loaderVer)
    inputs.property("kotlin_loader_version", kotlinModVer)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to version,
            "minecraft_version" to mcVer,
            "loader_version" to loaderVer,
            "kotlin_loader_version" to kotlinModVer
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
    val projectName = project.name
    inputs.property("projectName", projectName)
    from("LICENSE") {
        rename { "${it}_${projectName}" }
    }
}

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = "JAYD9vCA" // This can be the project ID or the slug. Either will work!
    syncBodyFrom = rootProject.file("README.md").readText()

    versionNumber = version.toString()
    versionType = "release" // `release`, `beta` or `alpha`

    (project.property("minecraft_publish") as String)
        .split(" ")
        .forEach(gameVersions::add)

    uploadFile.set(tasks.jar)
    loaders.add("fabric")

    dependencies { // A special DSL for creating dependencies
        // scope.type
        // The scope can be `required`, `optional`, `incompatible`, or `embedded`
        // The type can either be `project` or `version`
        required.project("fabric-api") // Creates a new required dependency on Fabric API
        required.project("fabric-language-kotlin")
    }
}
