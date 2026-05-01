import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("me.modmuss50.mod-publish-plugin") version "1.1.0"
}

val obfuscated = sc.current.parsed < "26.1"
plugins.apply(if(obfuscated) "net.fabricmc.fabric-loom-remap" else "net.fabricmc.fabric-loom")
val loom = the<LoomGradleExtensionAPI>()
val modImplementation = if(obfuscated) configurations.named("modImplementation") else configurations.implementation
val modJar = if(obfuscated) tasks.named<Zip>("remapJar") else tasks.named<Zip>("jar")

version = "${property("mod_version")}+${sc.current.version}"

base {
    archivesName = rootProject.name
}

val targetJavaVersion = project.property("java_version") as String
java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
    val j = JavaVersion.valueOf("VERSION_$targetJavaVersion")
    targetCompatibility = j
    sourceCompatibility = j
}

extensions.configure<LoomGradleExtensionAPI>() {
    splitEnvironmentSourceSets()

    mods {
        register(project.name) {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

dependencies {
    // To change the versions see the gradle.properties file
    "minecraft"("com.mojang:minecraft:${sc.current.version}")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_version")}")

    if (obfuscated) {
        "mappings"(loom.officialMojangMappings())
    }
}

tasks.processResources {
    val version = project.version
    val mcVer = project.property("minecraft_version_range")!!
    val loaderVer = project.property("loader_version")!!
    val kotlinModVer = project.property("kotlin_loader_version")!!
    inputs.property("version", version)
    inputs.property("minecraft_version_range", mcVer)
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

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(modJar.flatMap { it.archiveFile } /*, remapSourcesJar.map { it.archiveFile }*/)
    into(rootProject.layout.buildDirectory.file("libs"))
    dependsOn("build")
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release.set(Integer.valueOf(targetJavaVersion))
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion))
}

tasks.jar {
    val projectName = project.name
    inputs.property("projectName", projectName)
    from("LICENSE") {
        rename { "${it}_${projectName}" }
    }
}

publishMods {
    file = modJar.flatMap { it.archiveFile }
    displayName = "${property("mod_version")} for ${sc.current.version}"
    version = property("mod_version") as String
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE
    modLoaders.add("fabric")

    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null

    modrinth {
        projectId = "JAYD9vCA"
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(property("minecraft_publish").toString().split(' '))
        requires {
            slug = "fabric-api"
        }
        requires {
            slug = "fabric-language-kotlin"
        }
    }
}
