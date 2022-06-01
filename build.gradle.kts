import java.net.URI

plugins {
    kotlin("jvm") version "1.6.21" apply false
    kotlin("multiplatform") version "1.6.21" apply false
    kotlin("plugin.spring") version "1.6.21" apply false
    kotlin("plugin.serialization") version "1.6.21" apply false
    id("org.springframework.boot") version "2.6.7" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("com.github.node-gradle.node") version "3.2.1" apply false
    id("org.openapi.generator") version "5.4.0" apply false
}

group = "com.katonaaron"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven { url = URI("https://jitpack.io") }
    }
//    apply(plugin = "org.jlleitschuh.gradle.ktlint")
//    apply(plugin = "java")

//    dependencies {
// //        implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.13.3")
//    }

//    java {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }
}
