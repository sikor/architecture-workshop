plugins {
    id("java")
    id("org.ysb33r.terraform")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
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
    implementation(project(":launcher"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<AbstractRemoteTestTask>("remoteTest") {
    group = "verification"
    description = "Runs tests with environment configured via Terraform outputs"
    terraformToEnvMappings.set(
        mapOf(
            "token_uri" to "TOKEN_URI",
            "e2e_client_id" to "CLIENT_ID",
            "e2e_client_secret" to "CLIENT_SECRET",
            "events_app_url" to "EVENTS_API_BASE_URL",
            "aggregator_app_url" to "AGGREGATOR_API_BASE_URL",
            "events_app_client_credentials_scope" to "EVENTS_APP_SCOPE",
            "aggregator_app_client_credentials_scope" to "AGGREGATOR_APP_SCOPE"
        )
    )

}