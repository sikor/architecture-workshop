plugins {
    `java-library`
    id("io.spring.dependency-management")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.4.5") // or your chosen version
    }
}

dependencies {
    api(project(":observability"))
    api("ch.qos.logback:logback-classic:1.5.18")
    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.5"))
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-oauth2-client")
    api("org.springframework.boot:spring-boot-starter-jdbc")
    api("org.postgresql:postgresql")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.flywaydb:flyway-database-postgresql")
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    api("io.opentelemetry:opentelemetry-api:1.33.0")
    api("io.micrometer:micrometer-registry-otlp")
    runtimeOnly("io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0")



    testImplementation("org.springframework.boot:spring-boot-starter-test")

}