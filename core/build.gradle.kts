plugins {
    java
    kotlin("jvm") version "1.8.0"
}

group = "io.github.herrromich"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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