plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    application
}


dependencies {
    implementation(project(":annotation"))
    ksp(project(":processor"))

    testImplementation(libs.junit)
}

application {
    // Define the main class for the application.
    mainClass = "io.github.naitaku.databuilder.AppKt"
}