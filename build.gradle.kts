plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "com.github.andreilisun"
version = "1.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("com.github.andreilisun.ipa.Main")
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:4.2.2")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
}