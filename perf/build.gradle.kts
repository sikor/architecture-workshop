plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1" // for fatJar
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.5"))

    testImplementation("us.abstracta.jmeter:jmeter-java-dsl:1.29.1") {
        exclude("org.apache.jmeter", "bom")
    }
    // Add internal modules
    implementation(project(":commons")) // For token logic reuse
}

application {
    mainClass.set("com.archiwork.PerformanceTest")
}

tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("fatJar") {
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}
