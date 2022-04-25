import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.android.build.gradle.LibraryExtension

private fun BaseExtension.android() {
    compileSdkVersion(31)
    defaultConfig {
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }
}

fun Project.kotlinMultiplatform(
    explicitMode: Boolean = true
) {
    extensions.configure<KotlinMultiplatformExtension> {
        android {
            publishAllLibraryVariants()
        }
        jvm("desktop")

        sourceSets {
            /* Source sets structure
            common
              ├─ jvm
                  ├─ android
                  ├─ desktop
             */
            val commonMain by getting
            val commonTest by getting

            val jvmMain by creating {
                dependsOn(commonMain)
            }
            val jvmTest by creating {
                dependsOn(commonTest)
            }

            val androidMain by getting {
                dependsOn(jvmMain)
            }
            val androidTest by getting {
                dependsOn(jvmTest)
            }

            val desktopMain by getting {
                dependsOn(jvmMain)
            }
            val desktopTest by getting {
                dependsOn(jvmTest)
            }
        }
    }

    extensions.configure<LibraryExtension> {
        android()
        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.setup(explicitMode)
    }

}

private fun KotlinJvmOptions.setup(
    enableExplicitMode: Boolean
) {
    jvmTarget = JavaVersion.VERSION_1_8.toString()

    if (enableExplicitMode) freeCompilerArgs += "-Xexplicit-api=strict"
}
