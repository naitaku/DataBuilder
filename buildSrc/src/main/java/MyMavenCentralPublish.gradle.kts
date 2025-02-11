plugins {
    `maven-publish`
    signing
    id("tech.yanand.maven-central-publish")
    id("com.palantir.git-version")
}

val gitVersion: groovy.lang.Closure<String> by extra

group = "io.github.naitaku.databuilder"
version = gitVersion()

publishing {
    publications {
        register<MavenPublication>("Library") {
            afterEvaluate {
                from(components["java"])
            }
            pom {
                name = "DataBuilder"
                description = "Generate Builder Pattern class from Kotlin Data Class"
                url = "https://github.com/naitaku/DataBuilder"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "naitaku"
                        name = "Takuto Naito"
                        email = "naitaku@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/naitaku/DataBuilder.git"
                    developerConnection = "scm:git:ssh://github.com:naitaku/DataBuilder.git"
                    url = "https://github.com/naitaku/DataBuilder"
                }
            }
        }
    }
}
signing {
    val signingPrivateKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingPrivateKey, signingPassword)
    sign(publishing.publications["Library"])
}
mavenCentral {
    val mavenCentralToken: String? by project
    authToken = mavenCentralToken
}
