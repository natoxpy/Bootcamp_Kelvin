plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":generic-user-interface"))
}

application {
    mainClass.set("net.example.demo.Demo")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
