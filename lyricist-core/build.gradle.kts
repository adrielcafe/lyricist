plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "cafe.adriel.lyricist.core"
}

kotlinMultiplatform()

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines)
            }
        }
    }
}
