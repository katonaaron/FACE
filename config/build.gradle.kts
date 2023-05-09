val hoplite_version: String by project
val koin_version: String by project
val owlapi_version: String by project
val hermit_version: String by project
val slf4j_version: String by project

plugins {
    kotlin("jvm")
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

dependencies {
    // Project
    api(project(":fact-checker-api"))
    api(project(":fact-checker-application"))
    api(project(":core"))
    api(project(":ext"))
    implementation(project(":utils"))

    // Kotlin
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))


    // Hoplite - for configurations
    implementation("com.sksamuel.hoplite:hoplite-core:$hoplite_version")
    implementation("com.sksamuel.hoplite:hoplite-hocon:$hoplite_version")


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
