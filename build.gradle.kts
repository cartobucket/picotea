plugins {
    kotlin("jvm") version "2.1.0"
    application
}

allprojects {
    group = "com.cartobucket"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation(project(":components"))
    implementation("info.picocli:picocli:4.7.6")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.cartobucket.examples.MainKt")
}
