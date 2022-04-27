plugins {
    kotlin("jvm")
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("net.sourceforge.owlapi:owlapi-osgidistribution:4.1.3")
    implementation("net.sourceforge.owlapi:owlexplanation:2.0.0")
}
