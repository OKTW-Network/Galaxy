import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //    "maven-publish"
    kotlin("jvm") version "1.3.50"
    id("fabric-loom") version "0.2.5-SNAPSHOT"
}

val version = "0.0.1"
val group = "one.oktw"

val proxyApiVersion = "0.1.0"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://jitpack.io")
    maven(url = "http://maven.fabricmc.net/") {
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
    kotlinOptions.jvmTarget = "1.8"
}

minecraft {
}

dependencies {
    // Core
    minecraft(group = "com.mojang", name = "minecraft", version = "1.14.4")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.14.4+build.14")
    modCompile(group = "net.fabricmc", name = "fabric-loader", version = "0.6.3+build.167")

    // fabric api/library
    modCompile(group = "net.fabricmc", name = "fabric-language-kotlin", version = "1.3.50+build.3")
    modCompile(group = "net.fabricmc.fabric-api", name = "fabric-commands", version = "0.1.0")
    modCompile(group = "net.fabricmc.fabric-api", name = "fabric-events-lifecycle-v0", version = "0.1.1+eff46b3db4")
    modCompile(group = "net.fabricmc.fabric-api", name = "fabric-api-base", version = "0.1.1+38a28ddb96")

    // galaxy api
    modCompile(group = "one.oktw", name = "galaxy-proxy", version = proxyApiVersion)

    // Jar in Jar
    include(group = "net.fabricmc.fabric-api", name = "fabric-commands", version = "0.1.0")
    include(group = "net.fabricmc.fabric-api", name = "fabric-events-lifecycle-v0", version = "0.1.1+eff46b3db4")
    include(group = "net.fabricmc.fabric-api", name = "fabric-api-base", version = "0.1.1+38a28ddb96")
    include(group = "one.oktw", name = "galaxy-proxy", version = proxyApiVersion)
    include(group = "org.mongodb", name = "bson", version = "3.11.0")

    // PSA: Some older mods, compiled on Loom 0.2.1, might have outdated Maven POMs.
    // You may need to force-disable transitiveness on them.
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

// configure the maven publication
//publishing {
//    publications {
//        mavenJava(MavenPublication) {
//            // add all the jars that should be included when publishing to maven
//            artifact(jar) {
//                builtBy remapJar
//            }
//            artifact(sourcesJar) {
//                builtBy remapSourcesJar
//            }
//        }
//    }
//
//    // select the repositories you want to publish to
//    repositories {
//        // uncomment to publish to the local maven
//        // mavenLocal()
//    }
//}
