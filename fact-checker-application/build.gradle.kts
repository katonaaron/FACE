val koin_version: String by project
val kotlinx_serialization_version: String by project
val owlapi_version: String by project
val hermit_version: String by project
val slf4j_version: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.spring.dependency-management")
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    // Project
    api(project(":fact-checker-api"))
    implementation(project(":core"))
    implementation(project(":ext"))
    implementation(project(":utils"))

    // Kotlin
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:$kotlinx_serialization_version")
    testImplementation(kotlin("test"))

    // Koin
    // Koin for Kotlin apps
    implementation("io.insert-koin:koin-core:$koin_version")
    // SLF4J Logger for Koin
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    // Koin Test features
    testImplementation("io.insert-koin:koin-test:$koin_version")

    // OWL Api
    implementation("net.sourceforge.owlapi:owlapi-osgidistribution") {
        version {
            strictly(owlapi_version)
        }
    }

    // Hermit
    implementation("net.sourceforge.owlapi:org.semanticweb.hermit") {
        version {
            strictly(hermit_version)
        }
    }

    // Logging
    implementation("org.slf4j:slf4j-api:$slf4j_version")
}
