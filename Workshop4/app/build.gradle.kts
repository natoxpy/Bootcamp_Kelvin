plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "net.tarou.dealership.Program"
}

// Interactive menu, so the terminal has to be wired into the Java process:
//   ./gradlew --console=plain -q :app:run
//   ./gradlew :app:installDist && cd app && ./build/install/app/bin/app
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
