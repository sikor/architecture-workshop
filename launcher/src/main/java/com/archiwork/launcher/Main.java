package com.archiwork.launcher;

import com.archiwork.commons.properties.Env;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
        Properties props = Env.loadEnvFromClasspath("launcher-local.env");
        AppLauncher.startAppsWithDependenciesIfNeededAndPossible(
                getUrlFromConfig(props, "TOKEN_URI"),
                getUrlFromConfig(props, "EVENTS_API_BASE_URL"),
                getUrlFromConfig(props, "AGGREGATOR_API_BASE_URL")
        );
    }

    private static URL getUrlFromConfig(Properties props, String name) throws MalformedURLException {
        return URI.create(props.get(name).toString()).toURL();
    }
}
