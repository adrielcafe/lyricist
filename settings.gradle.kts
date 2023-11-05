dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev" )
    }
}

include(
    ":lyricist-compose",
    ":lyricist-core",
    ":lyricist-processor-compose",
    ":lyricist-processor-xml",
    ":lyricist-processor-properties",
    ":sample",
    ":sample-xml",
    ":sample-multi-module",
    ":sample-multiplatform",
)

// There's an issue when a module has the same name of the project
// https://github.com/gradle/gradle/issues/16608
//enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
