import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.0"
    id("net.fabricmc.fabric-loom") version "1.16.1"
}

val version = "0.0.1"
val group = "one.oktw"

val minecraftVersion = "26.1.2"
val fabricLoaderVersion = "0.19.2"
val fabricAPIVersion = "0.146.1+26.1.2"
val galaxyLibVersion = "9546e40"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://maven.fabricmc.net/")
}

base {
    archivesName.set("Galaxy")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_2_0
        languageVersion = KotlinVersion.KOTLIN_2_0
        jvmTarget = JvmTarget.JVM_25
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
    implementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")

    // fabric api
    implementation("net.fabricmc.fabric-api:fabric-api:${fabricAPIVersion}") {
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
