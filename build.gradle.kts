plugins {
    kotlin("jvm") version "2.2.20"
}

group = "ru.bmstu"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.platform:junit-platform-suite:1.8.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Test>("runAllTests") {
    useJUnitPlatform()
    filter {
        includeTestsMatching("AllTestsSuite")
    }
}

kotlin {
    jvmToolchain(21)
}