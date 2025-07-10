import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
    id("com.archiwork.remoteTest")
}

remoteTest {
    iacProjectName.set(":infra")
    appNameTfOutputName.set("events_app_name")
    resourceGroupTfOutputName.set("resource_group_name")
    deploymentArchiveTask.set(tasks.named<BootJar>("bootJar"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation(project(":commons"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}