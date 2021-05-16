import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //    "maven-publish"
    kotlin("jvm") version "1.5.0"
    id("fabric-loom") version "0.6-SNAPSHOT"
}

val version = "0.0.1"
val group = "one.oktw"

val galaxyLibVersion = "966a3109"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

base {
    archivesBaseName = "Galaxy"
}

java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        apiVersion = "1.5"
        languageVersion = "1.5"
        jvmTarget = "16"
    }
}

minecraft {
    accessWidener("src/main/resources/galaxy-tweak.accesswidener")
}

dependencies {
    // Core
    minecraft(group = "com.mojang", name = "minecraft", version = "1.16.5")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.16.5+build.9", classifier = "v2")
    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = "0.11.3")

    // fabric api
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "0.34.2+1.16")

    // galaxy api
    implementation(group = "one.oktw", name = "galaxy-lib", version = galaxyLibVersion)

    // Jar in Jar
    include(group = "one.oktw", name = "galaxy-lib", version = galaxyLibVersion, classifier = "all")
}

tasks.getByName<ProcessResources>("processResources") {
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand(Pair("version", version))
    }
}

tasks.getByName<Jar>("jar") {
    from("LICENSE")
}
