import org.gradle.kotlin.dsl.getByName
import tech.yanand.gradle.mavenpublish.MavenCentralExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    id("tech.yanand.maven-central-publish") version "1.3.0" apply false
    id("com.palantir.git-version") version "3.1.0"
}

val gitVersion: groovy.lang.Closure<String> by extra

subprojects {
    group = "io.github.naitaku.databuilder"
    version = gitVersion()

    if (project.name != "app") {
        apply(plugin = "maven-publish")
        apply(plugin = "signing")
        apply(plugin = "tech.yanand.maven-central-publish")
        configure<PublishingExtension> {
            publications {
                register<MavenPublication>("Library") {
                    afterEvaluate {
                        from(components["java"])
                    }
                    pom {
                        name.set("DataBuilder")
                        description.set("Generate Builder Pattern class from Kotlin Data Class")
                        url.set("https://github.com/naitaku/DataBuilder")
                        licenses {
                            license {
                                name.set("The Apache License, Version 2.0")
                                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                        developers {
                            developer {
                                id.set("naitaku")
                                name.set("Takuto Naito")
                                email.set("naitaku@gmail.com")
                            }
                        }
                        scm {
                            connection.set("scm:git:git://github.com/naitaku/DataBuilder.git")
                            developerConnection.set("scm:git:ssh://github.com:naitaku/DataBuilder.git")
                            url.set("https://github.com/naitaku/DataBuilder")
                        }
                    }
                }
            }
        }
        configure<SigningExtension> {
            useInMemoryPgpKeys(
                findProperty("signingPrivateKey")?.toString(),
                findProperty("signingPassword")?.toString()
            )
            sign(extensions.getByType<PublishingExtension>().publications["Library"])
        }
        configure<MavenCentralExtension> {
            authToken = findProperty("mavenCentralToken")?.toString()
        }
    }
}
