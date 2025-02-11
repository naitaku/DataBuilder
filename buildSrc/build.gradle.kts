plugins {
    `kotlin-dsl`
}
repositories {
    mavenCentral()
}
dependencies {
    implementation("tech.yanand.maven-central-publish:tech.yanand.maven-central-publish.gradle.plugin:1.3.0")
    implementation("com.palantir.gradle.gitversion:gradle-git-version:3.1.0")
}
