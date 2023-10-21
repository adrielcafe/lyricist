plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "cafe.adriel.lyricist.core"
}

kotlinMultiplatform()
