plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val springBootVersion: String by project
val gradleDockerComposeVersion: String by project
val springDependencyManagementVersion: String by project

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
    implementation("com.avast.gradle:gradle-docker-compose-plugin:$gradleDockerComposeVersion")
    implementation("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:$springDependencyManagementVersion")
}