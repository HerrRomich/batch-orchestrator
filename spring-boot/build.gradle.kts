plugins {
    java
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlinx.kover") version "0.7.1"
    id("java-test-fixtures")
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("plugin.spring") version "1.8.0" apply false
    id("org.springframework.boot") version "3.1.3"
}



group = "io.github.herrromich.batch-orchestrator"
version = "1.0-SNAPSHOT"

dependencies {
    compileOnly(project(":core"))
    compileOnly(project(":concurrent-orchestrator"))
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("org.springframework.boot:spring-boot")
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
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
