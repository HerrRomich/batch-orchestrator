plugins {
    kotlin("jvm") version "1.8.0"
}

group = "com.herrromich"
version = "0.0-SNAPSHOT"

dependencies {
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}