plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation("com.squareup:kotlinpoet:1.14.2")
                implementation("org.ocpsoft.prettytime:prettytime:4.0.4.Final")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.9.3")
                implementation(kotlin("test"))
            }
        }
        val desktopMain by getting
        val desktopTest by getting
    }
}
