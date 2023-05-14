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

include("core")
include("builder")
include("kotlin-dsl-builder")
include("concurrent-orchestrator")
include("reactive-events")
include("coroutines-orchestrator")
