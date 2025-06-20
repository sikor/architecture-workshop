rootProject.name = "architecture-workshops"
include("events")
include("aggregator")
include("commons")
include("e2e")
include("perf")
include("launcher")
include("infra")

pluginManagement {
    plugins {
        id("com.avast.gradle.docker-compose") version "0.17.12"
        id("org.springframework.boot") version "3.4.5"
        id("io.spring.dependency-management") version "1.1.4"
        id("org.gradle.kotlin.kotlin-dsl") version "5.2.0"
        id("org.ysb33r.terraform") version "2.0.0"
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