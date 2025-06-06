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
                logger.lifecycle("Health check failed: $url (attempt ${attempt+1}): HTTP ${connection.responseCode}")
            }
        } catch (ex: Exception) {
            logger.lifecycle("Health check error: $url (attempt ${attempt+1}): ${ex.message}")
        }

        Thread.sleep(delayMillis)
    }
    throw RuntimeException("App did not become healthy: $url")
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

    val process = ProcessBuilder(gradlew, ":$module:bootRun")
        .inheritIO()
        .start()
    processes.add(process)

    logger.lifecycle(process.info().toString())
    // Wait for health
    waitForHealthCheck("$address/actuator/health")
}


tasks.register("startApps") {
    doLast {
        runSpringApp("events", "EVENTS_API_BASE_URL")
        runSpringApp("aggregator", "AGGREGATOR_API_BASE_URL")

        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("Destroying apps")
            processes.forEach { it.destroy() }
        })
    }
}

tasks.register("start") {
    dependsOn(":local-env:start", "startApps")
}
