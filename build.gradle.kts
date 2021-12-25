import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //    "maven-publish"
    kotlin("jvm") version "1.6.0"
    id("fabric-loom") version "0.10-SNAPSHOT"
}

val version = "0.0.1"
val group = "one.oktw"

val galaxyLibVersion = "3193d04f"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

base {
    archivesName.set("Galaxy")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        apiVersion = "1.6"
        languageVersion = "1.6"
        jvmTarget = "17"
    }
}

loom {
    accessWidenerPath.set(file("src/main/resources/galaxy.accesswidener"))
}

dependencies {
    // Core
    minecraft(group = "com.mojang", name = "minecraft", version = "1.18.1")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.18.1+build.1", classifier = "v2")
    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = "0.12.9")

    // fabric api
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "0.44.0+1.18")

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
