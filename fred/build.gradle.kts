val owlapi_version: String by project
val slf4j_version: String by project
val http4k_version: String by project

plugins {
    kotlin("jvm")
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

dependencies {
    // Project
    implementation(project(":fact-checker-api"))
    implementation(project(":fact-checker-core"))
    implementation(project(":fact-checker-commons"))
    implementation(project(":sparql-dsl"))

    // Kotlin
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))


    // OWL Api
    implementation("net.sourceforge.owlapi:owlapi-osgidistribution") {
        version {
            strictly(owlapi_version)
        }
    }

    // HTTP4k
    implementation(platform("org.http4k:http4k-bom:$http4k_version"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-client-apache")

    // Logging
    implementation("org.slf4j:slf4j-api:$slf4j_version")
}
