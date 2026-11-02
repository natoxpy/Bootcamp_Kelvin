plugins {
    application
}

dependencies {
    implementation(project(":generic-user-interface"))
}

application {
    mainClass.set("net.example.demo.Demo")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
