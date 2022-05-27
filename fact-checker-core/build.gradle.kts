val owlapi_version: String by project
val owlexplanation_version: String by project
val hermit_version: String by project
val slf4j_version: String by project

plugins {
    kotlin("jvm")
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

dependencies {
    // Project
    implementation(project(":fact-checker-api"))
    implementation(project(":fact-checker-commons"))

    // Kotlin
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))

    // OWL Api
    implementation("net.sourceforge.owlapi:owlapi-osgidistribution") {
        version {
            strictly(owlapi_version)
        }
    }

    // OWL Explanation
    implementation("net.sourceforge.owlapi:owlexplanation") {
        version {
            strictly(owlexplanation_version)
        }
    }

    // Hermit - only for testing
    testImplementation("net.sourceforge.owlapi:org.semanticweb.hermit") {
        version {
            strictly(hermit_version)
        }
    }

    // Logging
    implementation("org.slf4j:slf4j-api:$slf4j_version")


    // SparqlDL
    implementation("com.github.protegeproject:sparql-dl-api:80d430d439e17a691d0111819af2d3613e28d625")


    implementation("net.sf.extjwnl:extjwnl:2.0.5")
    implementation("net.sf.extjwnl:extjwnl-data-wn31:1.2")

    // UNUSED - NLP library
    implementation("edu.stanford.nlp:stanford-corenlp:4.4.0")
    implementation("edu.stanford.nlp:stanford-corenlp:4.4.0:models")
}
