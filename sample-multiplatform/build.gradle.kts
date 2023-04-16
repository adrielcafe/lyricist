import com.google.devtools.ksp.gradle.KspTask
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.compose.desktop.application.tasks.AbstractNativeMacApplicationPackageTask
import org.jetbrains.compose.experimental.dsl.IOSDevices
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}

kotlinMultiplatform(
    withKotlinExplicitMode = false,
    // this is required for the Compose iOS Application DSL expect a `uikit` target name.
    iosPrefixName = "uikit",
)

android {
    namespace = "cafe.adriel.lyricist.sample.multiplatform"
}

kotlin {
    val macOsConfiguation: KotlinNativeTarget.() -> Unit = {
        binaries {
            executable {
                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal"
                )
            }
        }
    }
    macosX64(macOsConfiguation)
    macosArm64(macOsConfiguation)
    val uikitConfiguration: KotlinNativeTarget.() -> Unit = {
        binaries {
            executable() {
                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                    "-linker-option", "-framework", "-linker-option", "CoreText",
                    "-linker-option", "-framework", "-linker-option", "CoreGraphics"
                )
            }
        }
    }
    iosX64("uikitX64", uikitConfiguration)
    iosArm64("uikitArm64", uikitConfiguration)
    iosSimulatorArm64("uikitSimulatorArm64", uikitConfiguration)

    js(IR) {
        browser()
        binaries.executable()
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.material)
                implementation(compose.runtime)

                implementation(project(":lyricist"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.appCompat)
                implementation(libs.compose.activity)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}


dependencies {
    add("kspCommonMainMetadata", project(":lyricist-processor"))
}

// workaround for KSP only in Common Main.
// https://github.com/google/ksp/issues/567
tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().all {
    if(name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}

android {
    defaultConfig {
        applicationId = "cafe.adriel.lyricist.sample.multiplatform"
    }

    //ksp configuration
    sourceSets {
        getByName("debug") {
            val kspSource = "android/androidDebug"
            java.srcDir("build/generated/ksp/$kspSource/kotlin/")
        }
        getByName("release") {
            val kspSource = "android/androidRelease"
            java.srcDir("build/generated/ksp/$kspSource/kotlin/")
        }
    }
}

ksp {
    arg("lyricist.internalVisibility", "true")
}

compose.desktop {
    application {
        mainClass = "cafe.adriel.lyricist.sample.multiplatform.AppKt"
        nativeDistributions {
            targetFormats(Dmg, Msi, Deb)
            packageName = "jvm"
            packageVersion = "1.0.0"
        }
    }
}

compose.desktop.nativeApplication {
    targets(kotlin.targets.getByName("macosX64"))
    distributions {
        targetFormats(Dmg)
        packageName = "MultiplatformSample"
        packageVersion = "1.0.0"
    }
}

afterEvaluate {
    val baseTask = "createDistributableNative"
    listOf("debug", "release").forEach {
        val createAppTaskName = baseTask + it.capitalize() + "macosX64".capitalize()

        val createAppTask = tasks.findByName(createAppTaskName) as? AbstractNativeMacApplicationPackageTask?
            ?: return@forEach

        val destinationDir = createAppTask.destinationDir.get().asFile
        val packageName = createAppTask.packageName.get()

        tasks.create("runNative" + it.capitalize()) {
            group = createAppTask.group
            dependsOn(createAppTaskName)
            doLast {
                ProcessBuilder("open", destinationDir.absolutePath + "/" + packageName + ".app").start().waitFor()
            }
        }
    }
}

compose.experimental {
    uikit.application {
        bundleIdPrefix = "cafe.adriel.lyricist"
        projectName = "MultiplatformSample"
        deployConfigurations {
            simulator("IPhone8") {
                device = IOSDevices.IPHONE_8
            }
            simulator("IPad") {
                device = IOSDevices.IPAD_MINI_6th_Gen
            }
        }
    }
    web.application {}
}
