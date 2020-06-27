import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //    "maven-publish"
    kotlin("jvm") version "1.3.72"
    id("fabric-loom") version "0.4-SNAPSHOT"
}

val version = "0.0.1"
val group = "one.oktw"

val fabricVersion = "0.13.1+build.370-1.16"
val proxyApiVersion = "0.2.0"
val kotlinVersion = "1.3.72"
val coroutinesVersion = "1.3.7"

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
    minecraft(group = "com.mojang", name = "minecraft", version = "1.16.1")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.16.1+build.1", classifier = "v2")
    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = "0.8.8+build.202")

    // fabric api/library
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutinesVersion)
//    modImplementation(group = "net.fabricmc", name = "fabric-language-kotlin", version = "1.3.71+build.1")
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = fabricVersion)

    // galaxy api
    modImplementation(group = "one.oktw", name = "galaxy-proxy", version = proxyApiVersion)

    // Jar in Jar
    include(kotlin("stdlib", kotlinVersion))
    include(kotlin("stdlib-jdk7"))
    include(kotlin("stdlib-jdk8"))
    include(kotlin("reflect"))
    include(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutinesVersion)
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
