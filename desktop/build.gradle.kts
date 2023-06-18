import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
    id("org.jetbrains.compose")
    kotlin("kapt")
}

kotlin {
    jvm {
        jvmToolchain(19)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":common"))
                implementation("org.apache.pdfbox:pdfbox:3.0.0-RC1")
                implementation("org.apache.pdfbox:xmpbox:3.0.0-RC1")
                implementation("org.slf4j:slf4j-api:2.0.7")
                implementation("org.slf4j:slf4j-jdk14:2.0.7")
                implementation("org.yaml:snakeyaml:2.0")
                implementation("com.opencsv:opencsv:5.7.1")
                implementation("javax.xml.bind:jaxb-api:2.3.1")
                compileOnly("com.squareup:kotlinpoet:1.14.2")
                //implementation("com.github.ajalt.clikt:clikt:3.5.2")
                kapt {
                    annotationProcessors("io.pdfx.common.metadata.processing.AnnotationProcessor")
                }
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.9.3")
                implementation(kotlin("test"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.pdfx.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb, TargetFormat.AppImage)
            packageName = "pdfx-me"
            description = "PDFx Metadata Editor"
            packageVersion = "3.0.0"

            windows {
                iconFile.set(project.file("src/jvmMain/resources/icon-win.ico"))
                menu = true
                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "6565BEAD-713A-4DE7-A469-6B10FC4A6861"
            }

            macOS {
                iconFile.set(project.file("src/jvmMain/resources/icon-mac.icns"))
            }

            linux {
                iconFile.set(project.file("src/jvmMain/resources/icon-lin.png"))
            }
        }

        buildTypes.release {
            proguard {
                configurationFiles.from(project.file("compose-desktop.pro"))
            }
        }
    }
}
