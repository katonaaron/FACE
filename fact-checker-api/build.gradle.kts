val owlapi_version: String by project

plugins {
    kotlin("jvm")
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))

    // OWL Api
    implementation("net.sourceforge.owlapi:owlapi-osgidistribution") {
        version {
            strictly(owlapi_version)
        }
    }

}
