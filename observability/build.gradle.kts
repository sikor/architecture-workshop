plugins {
    id("java-library")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    api("io.opentelemetry:opentelemetry-api:1.33.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("org.snakeyaml:snakeyaml-engine:2.7")
}
