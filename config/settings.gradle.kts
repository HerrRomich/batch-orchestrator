rootProject.name = "config"

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
