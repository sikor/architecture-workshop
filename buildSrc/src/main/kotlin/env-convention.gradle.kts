
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

val extension = project.extensions.create("envConvention", EnvConventionExtension::class.java)

val envFileName = "${project.name}-local.env"
val envFile = project.file(envFileName)
if (envFile.exists()) {
    val envVars = loadEnvFileWithDefaults(envFile)
    extension.envVars = envVars
    logger.lifecycle("Env file found for '${project.name}' at: $envFile")

    // Apply env vars to Test tasks
    project.tasks.withType<Test>().configureEach {
        environment(envVars)
    }

    // Apply env vars to JavaExec tasks (for bootRun, etc)
    project.tasks.withType<JavaExec>().configureEach {
        environment(envVars)
    }
}