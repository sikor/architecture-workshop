plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {

}

tasks.withType<Test> {
    useJUnitPlatform()
}