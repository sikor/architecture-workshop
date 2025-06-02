plugins {
    id("java")
    id("env-convention")
}

dependencies {

    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.5"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("io.rest-assured:rest-assured:5.5.2")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.4.5")


    // Include your modules
    implementation(project(":commons"))
    implementation(project(":events"))
    implementation(project(":aggregator"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("e2eTest") {
    dependsOn(":events:build", ":aggregator:build", ":e2e:test")
}

tasks.named("compileTestJava") {
    dependsOn(":events:classes", ":aggregator:classes", ":commons:classes")
}