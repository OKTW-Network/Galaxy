import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //    "maven-publish"
    kotlin("jvm") version "1.3.71"
    id("fabric-loom") version "0.2.7-SNAPSHOT"
}

val version = "0.0.1"
val group = "one.oktw"

val fabricVersion = "0.1.2+b7f9825d0c"
val proxyApiVersion = "0.1.0"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://jitpack.io")
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
        jvmTarget = "13"
    }
}

minecraft {
}

dependencies {
    // Core
    minecraft(group = "com.mojang", name = "minecraft", version = "1.15.2")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.15.2+build.15", classifier = "v2")
    modCompile(group = "net.fabricmc", name = "fabric-loader", version = "0.8.2+build.194")

    // fabric api/library
    modImplementation(group = "net.fabricmc", name = "fabric-language-kotlin", version = "1.3.71+build.1")
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api-base", version = fabricVersion)
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-commands-v0", version = fabricVersion)
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-events-lifecycle-v0", version = fabricVersion)

    // galaxy api
    modImplementation(group = "one.oktw", name = "galaxy-proxy", version = proxyApiVersion)

    // Jar in Jar
    include(group = "net.fabricmc.fabric-api", name = "fabric-api-base", version = fabricVersion)
    include(group = "net.fabricmc.fabric-api", name = "fabric-commands-v0", version = fabricVersion)
    include(group = "net.fabricmc.fabric-api", name = "fabric-crash-report-info-v1", version = fabricVersion)
    include(group = "net.fabricmc.fabric-api", name = "fabric-events-lifecycle-v0", version = fabricVersion)
    include(group = "one.oktw", name = "galaxy-proxy", version = proxyApiVersion, classifier = "all")
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
