plugins {
    java
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlinx.kover") version "0.7.1"
}

group = "io.github.herrromich.batch-orchestrator"
version = "1.0-SNAPSHOT"

dependencies {
    compileOnly(project(":core"))
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
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

