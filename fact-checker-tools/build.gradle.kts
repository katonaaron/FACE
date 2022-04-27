plugins {
    kotlin("jvm")
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":fact-checker-commons"))
    implementation(project(":fact-checker-api"))
    implementation(project(":owl-verbalizer"))

    implementation(kotlin("stdlib"))
    implementation("net.sourceforge.owlapi:owlapi-osgidistribution:4.1.3")
    implementation("net.sourceforge.owlapi:org.semanticweb.hermit:1.3.8.413")
    implementation(platform("org.http4k:http4k-bom:4.25.8.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-client-apache")

    implementation(files("lib/logmap/logmap-matcher-4.0.jar"))

    testImplementation(kotlin("test"))
}
