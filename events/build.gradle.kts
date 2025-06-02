plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
    id("env-convention")
}

dependencies {
    implementation(project(":commons"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}