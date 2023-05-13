plugins {
    java
    kotlin("jvm") version "1.8.0"
}

dependencies {
    compileOnly("io.github.herrromich:core")
    compileOnly("io.github.herrromich:config")
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    testCompileOnly("io.github.herrromich:core")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
