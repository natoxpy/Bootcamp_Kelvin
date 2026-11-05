plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":generic-user-interface"))
	implementation("com.fasterxml.jackson.core:jackson-core:2.22.4")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.20.0")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

application {
    mainClass.set("net.example.demo.Demo")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
