import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //    "maven-publish"
    kotlin("jvm") version "1.4.21"
    id("fabric-loom") version "0.5-SNAPSHOT"
}

val version = "0.0.1"
val group = "one.oktw"

val galaxyLibVersion = "8bb519a9"

repositories {
    mavenCentral()
    jcenter()
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
        apiVersion = "1.4"
        languageVersion = "1.4"
        jvmTarget = "14"
        useIR = true
    }
}

minecraft {
    accessWidener("src/main/resources/galaxy-tweak.accesswidener")
}

dependencies {
    // Core
    minecraft(group = "com.mojang", name = "minecraft", version = "1.16.4")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.16.4+build.7", classifier = "v2")
    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = "0.10.8")

    // fabric api
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "0.29.3+1.16")

    // galaxy api
    implementation(group = "one.oktw", name = "galaxy-lib", version = galaxyLibVersion)

    // Jar in Jar
    include(group = "one.oktw", name = "galaxy-lib", version = galaxyLibVersion, classifier = "all")
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
