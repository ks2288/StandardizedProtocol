val auxiName: String by project
val auxiVersion: String by project
val jbAnnotationsVersion: String by project
val jbAnnotationsName: String by project
val kotlinxCoroutinesTestName: String by project
val kotlinxVersion: String by project


plugins {
    kotlin("jvm") version "1.9.20"
    id("maven-publish")
}

group = "com.github.ks288"
version = "1.0.0"

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("$kotlinxCoroutinesTestName:$kotlinxVersion")
    api("$jbAnnotationsName:$jbAnnotationsVersion")
    api("$auxiName:$auxiVersion")
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}