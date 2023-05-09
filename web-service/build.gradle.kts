import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val koin_version: String by project

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.github.johnrengelman.processes") version "0.5.0"
    id("org.springdoc.openapi-gradle-plugin") version "1.3.3"
    kotlin("jvm")
    kotlin("plugin.spring")
}

group = "com.katonaaron"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":config"))

    // Koin
    // Koin for Kotlin apps
    implementation("io.insert-koin:koin-core:$koin_version")
    // SLF4J Logger for Koin
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    // Testing
    testImplementation("io.insert-koin:koin-test:$koin_version")


    // HOCON property source
    implementation("com.github.zeldigas:spring-hocon-property-source:0.4.0")

    // Springdoc Openapi
    implementation("org.springdoc:springdoc-openapi-ui:1.6.8")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.8")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
