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
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.5") // or your chosen version
    }
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-jdbc")
    api("org.postgresql:postgresql")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("com.h2database:h2") // Embedded H2 database for local development
    api("org.flywaydb:flyway-core")
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    api("net.logstash.logback:logstash-logback-encoder:7.4")


    testImplementation("org.springframework.boot:spring-boot-starter-test")

}