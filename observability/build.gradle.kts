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

tasks.register<JavaExec>("generatePrometheusYaml") {
    group = "build"
    description = "Generates Prometheus alert definitions YAML"

    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.archiwork.observability.alerts.generator.Main")

    val alertsFileName =
        project.layout.buildDirectory.file("generated/prometheus/prometheus-alerts.yaml")

    args("prometheus", alertsFileName.get().asFile.absolutePath)

    outputs.file(alertsFileName)
}
