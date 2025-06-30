plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("remoteTestPlugin") {
            id = "com.archiwork.remoteTest"
            implementationClass = "com.archiwork.remoteTest.RemoteTestPlugin"
        }
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())                     // for org.gradle.api.*, Test, Task, etc.
    implementation("com.azure:azure-security-keyvault-secrets:4.10.0")
    implementation("com.azure:azure-identity:1.16.2")
}