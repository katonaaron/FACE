val owlapi_version: String by project
val hermit_version: String by project
val sparqldl_version: String by project
val slf4j_version: String by project

plugins {
    kotlin("jvm")
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))

    // OWL Api
    implementation("net.sourceforge.owlapi:owlapi-osgidistribution") {
        version {
            strictly(owlapi_version)
        }
    }

    // Hermit - only for testing
    testImplementation("net.sourceforge.owlapi:org.semanticweb.hermit") {
        version {
            strictly(hermit_version)
        }
    }

    // SparqlDL
    implementation("com.github.protegeproject:sparql-dl-api:$sparqldl_version")

    // Logging
    implementation("org.slf4j:slf4j-api:$slf4j_version")
}
