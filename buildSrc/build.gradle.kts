plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("remoteTestPlugin") {
            id = "com.archiwork.remoteTest"
            implementationClass = "RemoteTestPlugin"
        }
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())                     // for org.gradle.api.*, Test, Task, etc.
}