import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //    "maven-publish"
    kotlin("jvm") version "2.0.21"
    id("fabric-loom") version "1.8-SNAPSHOT"
}

val version = "0.0.1"
val group = "one.oktw"

val fabricVersion = "0.107.3+1.21.3"
val galaxyLibVersion = "c328418"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

base {
    archivesName.set("Galaxy")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_2_0
        languageVersion = KotlinVersion.KOTLIN_2_0
        jvmTarget = JvmTarget.JVM_21
    }
}

loom {
    accessWidenerPath.set(file("src/main/resources/galaxy.accesswidener"))
}

dependencies {
    // Core
    minecraft(group = "com.mojang", name = "minecraft", version = "1.21.3")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.21.3+build.2", classifier = "v2")
    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = "0.16.9")

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
