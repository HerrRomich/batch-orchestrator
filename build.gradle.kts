plugins {
    java
    kotlin("jvm") version "1.8.0"
}

group = "io.github.herrromich"
version = "0.0-SNAPSHOT"

dependencies {
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.slf4j:slf4j-simple:2.0.6")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}