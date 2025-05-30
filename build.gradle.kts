plugins {
    id("org.springframework.boot") version "3.4.5" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
}

allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

fun loadEnvFileWithDefaults(file: File): Map<String, String> {
    return file.readLines()
        .filter { it.isNotBlank() && !it.trim().startsWith("#") }
        .mapNotNull {
            val (key, value) = it.split("=", limit = 2)
            val trimmedKey = key.trim()
            val trimmedValue = value.trim()
            if (System.getenv(trimmedKey) == null) {
                trimmedKey to trimmedValue
            } else {
                null // skip, because it's already set in the environment
            }
        }.toMap()
}

subprojects {
    val envFileName = "${project.name}-local.env"

    tasks.withType<Test>().configureEach {
        val envFile = project.file("src/test/resources/$envFileName")
        if (envFile.exists()) {
            environment(loadEnvFileWithDefaults(envFile))
            logger.info("Env file found for '${project.name}' at: $envFile")
        }
    }

    tasks.withType<JavaExec>().configureEach {
        val envFile = project.file("src/main/resources/$envFileName")
        if (envFile.exists()) {
            environment(loadEnvFileWithDefaults(envFile))
            logger.info("Env file found for '${project.name}' at: $envFile")
        }
    }
}
