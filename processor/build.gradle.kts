plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    id("MyMavenCentralPublish")
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":annotation"))
    implementation(libs.symbol.processing.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
}