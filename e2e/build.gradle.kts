plugins {
    id("java")
    id("com.archiwork.remoteTest")
}

remoteTest {
    iacProjectName.set(":infra")
    keyVaultUrlTfOutputName.set(("key_vault_url"))
    environmentVariables {
        keyVaultToEnv("e2e-secret", "CLIENT_SECRET")
        tfOutputToEnv("token_uri", "TOKEN_URI")
        tfOutputToEnv("e2e_client_id", "CLIENT_ID")
        tfOutputToEnv("events_app_url", "EVENTS_API_BASE_URL")
        tfOutputToEnv("aggregator_app_url", "AGGREGATOR_API_BASE_URL")
        tfOutputToEnv("events_app_client_credentials_scope", "EVENTS_APP_SCOPE")
        tfOutputToEnv("aggregator_app_client_credentials_scope", "AGGREGATOR_APP_SCOPE")
    }
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
