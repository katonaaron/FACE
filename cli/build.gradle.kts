import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logback_version: String by project
val koin_version: String by project
val hoplite_version: String by project

plugins {
    kotlin("jvm")
    application
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    // Project dependencies
    implementation(project(":config"))
    implementation(project(":fact-checker-api"))
    implementation(project(":core"))
    implementation(project(":ext"))
    implementation(project(":utils"))
    implementation(project(":fact-checker-application"))

    // Kotlin
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
    testImplementation(kotlin("test"))

    implementation("net.sourceforge.owlapi:owlapi-osgidistribution:4.1.3")
    implementation("net.sourceforge.owlapi:org.semanticweb.hermit:1.3.8.413")

    // Koin
    // Koin for Kotlin apps
    implementation("io.insert-koin:koin-core:$koin_version")
    // SLF4J Logger for Koin
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    // Testing
    testImplementation("io.insert-koin:koin-test:$koin_version")

    // Hoplite - for configurations
    implementation("com.sksamuel.hoplite:hoplite-core:$hoplite_version")
    implementation("com.sksamuel.hoplite:hoplite-hocon:$hoplite_version")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

application {
    mainClass.set("com.katonaaron.factcheckercli.FactCheckerApplicationKt")
    applicationName = "factcheck"
}
