

val processes = mutableListOf<Process>()

fun runSpringApp(module: String) {
    logger.info("runSpringApp: $module")
    val gradlew = if (System.getProperty("os.name").startsWith("Windows")) "gradlew.bat" else "./gradlew"
    val process = ProcessBuilder(gradlew, ":$module:bootRun")
        .inheritIO()
        .start()
    processes.add(process)
}

tasks.register("startApps") {
    doLast {
        runSpringApp("events")
        runSpringApp("aggregator")

        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("Destroying apps")
            processes.forEach { it.destroy() }
        })
    }
}

tasks.register("start") {
    dependsOn(":local-env:start", "startApps")
}
