plugins {
    java
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlinx.kover") version "0.7.1"
    id("java-test-fixtures")
}

dependencies {
    compileOnly(project(":core"))
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("io.reactivex.rxjava3:rxjava:3.1.6")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}