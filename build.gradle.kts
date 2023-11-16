plugins {
    application
    kotlin("jvm") version "1.9.20"
}

group = "com.openphonics.data"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}
allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

dependencies {
    //Google Cloud APIS
    implementation("com.google.cloud:google-cloud-storage:2.29.1")
    implementation("com.google.cloud:google-cloud-translate:2.30.0")
    implementation("com.google.cloud:google-cloud-texttospeech:2.31.0")
    //Retrofit for API call
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //Testing
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}