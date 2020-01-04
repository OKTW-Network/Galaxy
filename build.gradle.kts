import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //    "maven-publish"
    kotlin("jvm") version "1.3.61"
    id("fabric-loom") version "0.2.6-SNAPSHOT"
}

val version = "0.0.1"
val group = "one.oktw"

val fabricVersion = "0.1.2+b7f9825de8"
val proxyApiVersion = "0.1.0"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://jitpack.io")
    maven(url = "https://maven.fabricmc.net/") {
        name = "Fabric"
    }
    maven(url = "https://kotlin.bintray.com/kotlinx") {
        name = "Kotlinx"
    }
}

base {
    archivesBaseName = "Galaxy"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        apiVersion = "1.3"
        languageVersion = "1.3"
        jvmTarget = "1.8"
    }
}

minecraft {
}

dependencies {
    // Core
    minecraft(group = "com.mojang", name = "minecraft", version = "1.15.1")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.15.1+build.23", classifier = "v2")
    modCompile(group = "net.fabricmc", name = "fabric-loader", version = "0.7.3+build.176")

    // fabric api/library
    modImplementation(group = "net.fabricmc", name = "fabric-language-kotlin", version = "1.3.61+build.2")
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api-base", version = fabricVersion)
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-commands-v0", version = fabricVersion)
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-events-lifecycle-v0", version = "0.1.1+eff46b3db4")

    // galaxy api
    modImplementation(group = "one.oktw", name = "galaxy-proxy", version = proxyApiVersion)

    // Jar in Jar
    include(group = "net.fabricmc.fabric-api", name = "fabric-api-base", version = fabricVersion)
    include(group = "net.fabricmc.fabric-api", name = "fabric-commands-v0", version = fabricVersion)
    include(group = "net.fabricmc.fabric-api", name = "fabric-events-lifecycle-v0", version = fabricVersion)
    include(group = "one.oktw", name = "galaxy-proxy", version = proxyApiVersion)
    include(group = "org.mongodb", name = "bson", version = "3.12.0")
}

tasks.getByName<ProcessResources>("processResources") {
    inputs.property("version", version)

    from(sourceSets.getByName("main").resources.srcDirs) {
        include("fabric.mod.json")
        expand(Pair("version", version))
    }

    from(sourceSets.getByName("main").resources.srcDirs) {
        exclude("fabric.mod.json")
    }
}

tasks.getByName<Jar>("jar") {
    from("LICENSE")
}
