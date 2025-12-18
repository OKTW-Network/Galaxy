import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //    "maven-publish"
    kotlin("jvm") version "2.2.20"
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
}

val version = "0.0.1"
val group = "one.oktw"

val minecraftVersion = "1.21.11"
val fabricLoaderVersion = "0.18.3"
val fabricAPIVersion = "0.140.0+1.21.11"
val galaxyLibVersion = "f4e1b25"

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

fabricApi {
    configureDataGeneration {
        createSourceSet = true
        modId = "galaxy"
    }
}

dependencies {
    // Core
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")

    // fabric api
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricAPIVersion}") {
        val gametest = fabricApi.module("fabric-gametest-api-v1", fabricAPIVersion) // Unused and cause client Registry remapping failed.
        exclude(gametest.group, gametest.name)
    }

    // galaxy api
    implementation("one.oktw:galaxy-lib:${galaxyLibVersion}")

    // Jar in Jar
    include("one.oktw:galaxy-lib:${galaxyLibVersion}:all")
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
