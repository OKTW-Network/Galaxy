import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //    "maven-publish"
    kotlin("jvm") version "1.9.22"
    id("fabric-loom") version "1.4-SNAPSHOT"
}

val version = "0.0.1"
val group = "one.oktw"

val fabricVersion = "0.92.0+1.20.4"
val galaxyLibVersion = "9a964eaf"

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
        apiVersion = "1.7"
        languageVersion = "1.7"
        jvmTarget = "17"
    }
}

loom {
    accessWidenerPath.set(file("src/main/resources/galaxy.accesswidener"))
}

dependencies {
    // Core
    minecraft(group = "com.mojang", name = "minecraft", version = "1.20.4")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.20.4+build.3", classifier = "v2")
    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = "0.15.3")

    // fabric api
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = fabricVersion) {
        val gametest = fabricApi.module("fabric-gametest-api-v1", fabricVersion) // Unused and cause client Registry remapping failed.
        exclude(gametest.group, gametest.name)
    }

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
