plugins {
    id("com.avast.gradle.docker-compose")
}

dockerCompose {
    useComposeFiles = listOf("docker-compose.yml")
    startedServices = listOf("postgresql", "keycloak")
}

tasks.register("start") {
    dependsOn("composeUp")
}

tasks.register("stop") {
    dependsOn("composeDown")
}