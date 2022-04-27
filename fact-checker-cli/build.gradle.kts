import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Project dependencies
    implementation(project(":fact-checker-api"))
    implementation(project(":fact-checker-core"))
    implementation(project(":fact-checker-tools"))
    implementation(project(":fact-checker-commons"))

    // Libraries
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")

    implementation("net.sourceforge.owlapi:owlapi-osgidistribution:4.1.3")
    implementation("net.sourceforge.owlapi:org.semanticweb.hermit:1.3.8.413")

    // Test Libraries
    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

application {
    mainClass.set("com.katonaaron.energy.FactCheckerApplicationKt")
    applicationName = "factcheck"
}
