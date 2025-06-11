package com.archiwork.launcher;

import java.io.IOException;

public class DockerComposeLauncher {

    private static final String COMPOSE_FILE = "../local-env/docker-compose.yml";

    private static boolean wasStarted = false;

    public static void start() {
        wasStarted = true;
        runCommand("up", "--detach", "--wait", "--wait-timeout", "60");
    }

    public static void stop() {
        if (wasStarted) {
            runCommand("down");
        }
    }

    private static void runCommand(String... args) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(buildComposeCommand(args));
        builder.inheritIO(); // optional: show logs

        try {
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0 && !"down".equals(args[0])) {
                throw new RuntimeException("docker-compose command failed: " + String.join(" ", args));
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to run docker-compose command", e);
        }
    }

    private static String[] buildComposeCommand(String... args) {
        String[] base = {"docker", "compose", "-f", COMPOSE_FILE};
        String[] fullCommand = new String[base.length + args.length];
        System.arraycopy(base, 0, fullCommand, 0, base.length);
        System.arraycopy(args, 0, fullCommand, base.length, args.length);
        return fullCommand;
    }
}
