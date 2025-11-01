plugins {
    kotlin("jvm")
}

group = "com.cartobucket"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("info.picocli:picocli:4.7.6")
}

kotlin {
    jvmToolchain(21)
}
