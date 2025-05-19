plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
}

dependencies {
    implementation(project(":commons"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}