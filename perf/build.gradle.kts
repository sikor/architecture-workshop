plugins {
    id("java")
    id("com.archiwork.remoteTest")
}

remoteTest {
    iacProjectName.set(":infra")
    keyVaultUrlTfOutputName.set(("key_vault_url"))
    environmentVariables {
        keyVaultToEnv("perf-secret", "CLIENT_SECRET")
        tfOutputToEnv("token_uri", "TOKEN_URI")
        tfOutputToEnv("perf_client_id", "CLIENT_ID")
        tfOutputToEnv("events_app_url", "EVENTS_API_BASE_URL")
        tfOutputToEnv("aggregator_app_url", "AGGREGATOR_API_BASE_URL")
        tfOutputToEnv("events_app_client_credentials_scope", "EVENTS_APP_SCOPE")
        tfOutputToEnv("aggregator_app_client_credentials_scope", "AGGREGATOR_APP_SCOPE")
    }
}


dependencies {
    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.5"))

    // Add internal modules
    testImplementation(project(":commons")) // For token logic reuse
    testImplementation(project(":launcher"))

    testImplementation("us.abstracta.jmeter:jmeter-java-dsl:1.29.1") {
        exclude("org.apache.jmeter", "bom") // Exclude missing BOM to avoid resolution errors
    }
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.4.5")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.test {
    useJUnitPlatform()
}