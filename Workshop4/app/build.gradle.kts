/*
 * Build for the Workshop 4w console car-dealership app.
 *
 * The app is a plain console program: the Java application plugin is the only plugin it needs,
 * and JUnit Jupiter is the only test dependency.
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    // Define the main class for the application.
    mainClass = "net.tarou.dealership.Program"
}

// An interactive console program needs the terminal wired into the Java process, which a JavaExec
// task does not do on its own:
//   ./gradlew --console=plain -q :app:run
// or build a launcher once and run it without Gradle at all:
//   ./gradlew :app:installDist
//   cd app && ./build/install/app/bin/app
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
