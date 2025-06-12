plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {

    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.5"))


    // Include your modules
    implementation(project(":commons"))
    implementation(project(":events"))
    implementation(project(":aggregator"))
}