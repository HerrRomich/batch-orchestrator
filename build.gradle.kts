plugins {
    kotlin("jvm") version "1.8.0"
}

group = "com.herrromich"
version = "0.0-SNAPSHOT"

dependencies {
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}