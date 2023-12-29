plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization")
}

kotlin {
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation("org.slf4j:slf4j-api:2.0.7")
                implementation("org.slf4j:slf4j-jdk14:2.0.7")
                compileOnly("com.squareup:kotlinpoet:1.14.2")
                implementation("org.ocpsoft.prettytime:prettytime:5.0.7.Final")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.9.3")
                implementation(kotlin("test"))
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation("org.apache.commons:commons-lang3:3.12.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
                implementation("io.ktor:ktor-client-core:2.3.1")
                implementation("io.ktor:ktor-client-java:2.3.1")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.1")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.1")
            }
        }
        val desktopTest by getting
    }
}
