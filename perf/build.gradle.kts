plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.5"))
    // Add internal modules
    testImplementation(project(":commons")) // For token logic reuse

    testImplementation("us.abstracta.jmeter:jmeter-java-dsl:1.29.1") {
        exclude("org.apache.jmeter", "bom") // Exclude missing BOM to avoid resolution errors
    }
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.4.5")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("perfTest") {
    dependsOn(":commons:build", ":perf:test")
}

tasks.named("compileTestJava") {
    dependsOn(":commons:classes")
}