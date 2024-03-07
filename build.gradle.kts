val jbAnnotationsVersion: String by project
val jbAnnotationsName: String by project
val kotlinxCoroutinesTestName: String by project
val kotlinxVersion: String by project


plugins {
    kotlin("jvm") version "1.9.20"
}

group = "dev.specter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("$jbAnnotationsName:$jbAnnotationsVersion")
    testImplementation("$kotlinxCoroutinesTestName:$kotlinxVersion")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

kotlin {
    jvmToolchain(17)
}