plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("com.vanniktech.maven.publish")
}

kotlinMultiplatform()

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":lyricist-core"))
                compileOnly(compose.runtime)
                compileOnly(compose.ui)
            }
        }
    }
}
