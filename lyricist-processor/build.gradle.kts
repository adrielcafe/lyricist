apply(from = "../kotlin-module.gradle")

plugins {
    id("com.vanniktech.maven.publish")
}

dependencies {
    add("implementation", libs.ksp)
    add("compileOnly", kotlin("reflect"))
}
