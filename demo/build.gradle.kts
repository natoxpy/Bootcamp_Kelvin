plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
}

application {
    mainClass.set("net.example.demo.Demo")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
