plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
    id("env-convention")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation(project(":commons"))

}

tasks.withType<Test> {
    useJUnitPlatform()
}