plugins {
    kotlin("jvm")
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))

    implementation("net.sourceforge.owlapi:owlapi-osgidistribution:4.1.3")
    implementation("com.github.protegeproject:sparql-dl-api:80d430d439e17a691d0111819af2d3613e28d625")
}
