rootProject.name = "batch-orchestrator"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.4.0")
}
