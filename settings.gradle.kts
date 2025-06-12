rootProject.name = "architecture-workshops"
include("events")
include("aggregator")
include("commons")
include("e2e")
include("perf")
include("launcher")

pluginManagement {
    plugins {
        id("com.avast.gradle.docker-compose") version "0.17.12"
        id("org.springframework.boot") version "3.4.5"
        id("io.spring.dependency-management") version "1.1.4"
        id("org.gradle.kotlin.kotlin-dsl") version "5.2.0"
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}