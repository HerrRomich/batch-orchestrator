plugins {
    java
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlinx.kover") version "0.7.1"
    id("java-test-fixtures")
}

group = "io.github.herrromich.batch-orchestrator"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.0")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.mockito:mockito-core:3.+")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testCompileOnly(testFixtures(project(":core")))

    testFixturesImplementation("io.github.microutils:kotlin-logging:3.0.5")
}

tasks {
    named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
        compilerOptions {
            freeCompilerArgs.add("-Xjvm-default=all")
        }
    }

    test {
        useJUnitPlatform()
    }
}
