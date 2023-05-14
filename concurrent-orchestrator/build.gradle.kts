plugins {
    java
    kotlin("jvm") version "1.8.0"
    id("java-test-fixtures")
}

group = "io.github.herrromich.batch-orchestrator"
version = "1.0-SNAPSHOT"

dependencies {
    compileOnly(project(":core"))
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    testImplementation(project(":core"))
    testImplementation(project(":builder"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.0")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
    testImplementation(testFixtures(project(":core")))
}

tasks.test {
    useJUnitPlatform()
}
