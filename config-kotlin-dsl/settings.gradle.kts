rootProject.name = "config-kotlin-dsl"

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

includeBuild("../core")
includeBuild("../config")
