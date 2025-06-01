rootProject.name = "architecture-workshops"
include("events")
include("aggregator")
include("commons")
include("e2e")
include("perf")

pluginManagement {
    plugins {
        id("com.avast.gradle.docker-compose") version "0.17.12"
        id("org.springframework.boot") version "3.4.5"
        id("io.spring.dependency-management") version "1.1.4"
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