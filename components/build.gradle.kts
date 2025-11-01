plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

group = "com.cartobucket.pico-tea"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("info.picocli:picocli:4.7.6")
}

kotlin {
    jvmToolchain(21)
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "com.cartobucket.pico-tea"
            artifactId = "picotea"
            version = project.version.toString()

            pom {
                name.set("Pico Tea Components")
                description.set("Terminal UI components for Picocli applications")
                url.set("https://github.com/cartobucket/pico-tea")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("cartobucket")
                        name.set("Cartobucket")
                        email.set("your-email@example.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/cartobucket/pico-tea.git")
                    developerConnection.set("scm:git:ssh://github.com/cartobucket/pico-tea.git")
                    url.set("https://github.com/cartobucket/pico-tea")
                }
            }
        }
    }

    repositories {
        maven {
            name = "CentralPortal"
            val releasesRepoUrl = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://central.sonatype.com/repository/maven-snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }
}

signing {
    val signingKey = System.getenv("GPG_PRIVATE_KEY")
    val signingPassword = System.getenv("GPG_PASSPHRASE")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}
