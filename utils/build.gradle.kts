val owlapi_version: String by project
val slf4j_version: String by project
val sparqldl_version: String by project

plugins {
    kotlin("jvm")
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))

    // SparQl-DL
    implementation("com.github.protegeproject:sparql-dl-api:$sparqldl_version")

    // OWL Api
    implementation("net.sourceforge.owlapi:owlapi-osgidistribution") {
        version {
            strictly(owlapi_version)
        }
    }

    // Logging
    implementation("org.slf4j:slf4j-api:$slf4j_version")
}
