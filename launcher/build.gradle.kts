import java.net.HttpURLConnection
import java.net.URL

plugins {
    id("env-convention")
}

fun waitForHealthCheck(url: String, retries: Int = 30, delayMillis: Long = 1000) {
    repeat(retries) { attempt ->
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 1000
            connection.readTimeout = 1000

            if (connection.responseCode == 200) {
                logger.lifecycle("Health check passed: $url")
                return
            } else {
                logger.lifecycle("Health check failed: $url (attempt ${attempt + 1}): HTTP ${connection.responseCode}")
            }
        } catch (ex: Exception) {
            logger.lifecycle("Health check error: $url (attempt ${attempt + 1}): ${ex.message}")
        }

        Thread.sleep(delayMillis)
    }
    throw RuntimeException("App did not become healthy: $url")
}

fun destroyProcesses(process: ProcessHandle) {
    process.destroy()
    process.onExit().get(2, TimeUnit.MINUTES)
    process.descendants().forEach {
        destroyProcesses(it)
    }
}

val processes = mutableListOf<Process>()
val envVars = extensions.getByType<EnvConventionExtension>().envVars

fun runSpringApp(module: String, envVar: String) {
    logger.lifecycle("runSpringApp: $module")
    val address = envVars[envVar]


    val gradlew = if (System.getProperty("os.name").startsWith("Windows")) {
        project.rootProject.projectDir.resolve("gradlew.bat").absolutePath
    } else {
        project.rootProject.projectDir.resolve("gradlew").absolutePath
    }

    val builder = ProcessBuilder(gradlew, ":$module:bootRun")
        .inheritIO()
    val process = builder.start()
    processes.add(process)

    logger.lifecycle(builder.command().toString())
    // Wait for health
    waitForHealthCheck("$address/actuator/health")
}


tasks.register("startApps") {
    doLast {
        runSpringApp("events", "EVENTS_API_BASE_URL")
        runSpringApp("aggregator", "AGGREGATOR_API_BASE_URL")
    }
}

tasks.register("stopApps") {
    doLast {
        logger.lifecycle("Destroying apps")
        processes.forEach { destroyProcesses(it.toHandle()) }
    }
}

tasks.register("start") {
    dependsOn(":local-env:start", "startApps")
}
