plugins {
    id("java-library")
    id("io.spring.dependency-management")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {

}

tasks.withType<Test> {
    useJUnitPlatform()
}s