plugins {
    application
}

dependencies {
    implementation(project(":generic-user-interface"))
    implementation(project(":renderer"))
}

application {
    mainClass.set("net.example.demo.Demo")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
