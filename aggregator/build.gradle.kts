plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
    id("application")
    id("com.archiwork.remoteTest")
}

application {
    mainClass.set("com.archiwork.aggregator.AggregatorApplication")
}


remoteTest {
    iacProjectName.set(":infra")
    appNameTfOutputName.set("aggregator_app_name")
    resourceGroupTfOutputName.set("resource_group_name")
    deploymentArchiveTask.set(tasks.named<Zip>("distZip"))
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