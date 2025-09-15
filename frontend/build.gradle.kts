import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "7.0.2"
}

node {
    
    version = "22.12.0"
    npmVersion = "10.9.0"
    download = true
}

tasks.register<NpmTask>("runBuild") {
    args.set(listOf("run", "build"))
    workingDir.set(project.layout.projectDirectory.asFile) // frontend-root
}

val backendStatic = layout.projectDirectory.dir("../src/main/resources/static")

tasks.register<Delete>("cleanStatic") {
    delete(backendStatic)
}

tasks.register<Copy>("copyWebApp") {
    dependsOn("runBuild", "cleanStatic")
    from(layout.projectDirectory.dir("dist"))
    into(backendStatic)
}

tasks.register("deployToSpring") {
    group = "build"
    description = "Builds frontend and copies dist/ into Spring static/"
    dependsOn("copyWebApp")
}
