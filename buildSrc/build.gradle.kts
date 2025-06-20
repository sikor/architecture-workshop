plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())                     // for org.gradle.api.*, Test, Task, etc.
    implementation("org.ysb33r.gradle:terraform-base-plugin:2.0.0")
}