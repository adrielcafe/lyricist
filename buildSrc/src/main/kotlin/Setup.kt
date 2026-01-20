import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.kotlin.dsl.*
import com.android.build.gradle.LibraryExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

private val targetJavaVersion = JavaVersion.VERSION_11
private val targetJvmVersion = JvmTarget.JVM_11

private fun BaseExtension.android() {
    compileSdkVersion(34)
    defaultConfig {
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
}

fun Project.kotlinMultiplatform(
    withKotlinExplicitMode: Boolean = true,
) {
    plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper> {
        extensions.configure<KotlinMultiplatformExtension> {
            if (withKotlinExplicitMode) {
                explicitApi()
            }

            androidTarget {
                if (project.plugins.hasPlugin("com.vanniktech.maven.publish")) {
                    publishLibraryVariants("release")
                }
            }
            jvm("desktop")

            js(IR) {
                browser()
            }
            @OptIn(ExperimentalWasmDsl::class)
            wasmJs {
                browser()
            }

            // Use default hierarchy template for native targets
            applyDefaultHierarchyTemplate()

            macosX64()
            macosArm64()
            iosX64()
            iosArm64()
            iosSimulatorArm64()
        }

        findAndroidExtension().apply {
            android()
            sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }

        // Configure JVM compilation using modern API
        configureJvmCompilation(withKotlinExplicitMode)
    }
}

private fun Project.configureJvmCompilation(withKotlinExplicitMode: Boolean) {
    // Java Plugin Configuration
    plugins.withType<JavaPlugin> {
        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = targetJavaVersion
            targetCompatibility = targetJavaVersion
        }
    }

    // Android Configuration
    plugins.withId("com.android.library") {
        findAndroidExtension().apply {
            compileOptions {
                sourceCompatibility = targetJavaVersion
                targetCompatibility = targetJavaVersion
            }
        }
    }

    plugins.withId("com.android.application") {
        findAndroidExtension().apply {
            compileOptions {
                sourceCompatibility = targetJavaVersion
                targetCompatibility = targetJavaVersion
            }
        }
    }

    // Kotlin Multiplatform Configuration
    extensions.findByType<KotlinMultiplatformExtension>()?.apply {
        targets.configureEach {
            compilations.configureEach {
                compileTaskProvider.configure {
                    compilerOptions {
                        if (this !is KotlinJvmCompilerOptions) return@compilerOptions
                        jvmTarget.set(targetJvmVersion)
                        if (withKotlinExplicitMode) {
                            freeCompilerArgs.add("-Xexplicit-api=strict")
                        }
                    }
                }
            }
        }
    }

    // Fallback para tasks Android/JVM Kotlin
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(targetJvmVersion)
            if (withKotlinExplicitMode) {
                freeCompilerArgs.add("-Xexplicit-api=strict")
            }
        }
    }
}

private fun Project.findAndroidExtension(): BaseExtension = extensions.findByType<LibraryExtension>()
    ?: extensions.findByType<com.android.build.gradle.AppExtension>()
    ?: error("Could not find Android application or library plugin applied on module $name")
