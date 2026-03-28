import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") version "2.1.20"
    id("org.jetbrains.compose") version "1.8.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
}

group = "com.rastgeletor"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation("org.xerial:sqlite-jdbc:3.46.1.0")
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.resources {
    packageOfResClass = "com.rastgeletor"
}

compose.desktop {
    application {
        mainClass = "Rastgeletor"

        nativeDistributions {
            targetFormats(TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.Msi, TargetFormat.Exe, TargetFormat.AppImage)
            packageName = "Rastgeletor"
            packageVersion = "1.0.0"
            description = "Öğrenci Seçme ve Gruplandırma Aracı"
            vendor = "hllsygn"
            
            buildTypes.release.proguard {
                isEnabled.set(false)
            }

            linux {
                iconFile.set(project.file("src/commonMain/composeResources/drawable/app_icon.png"))
                packageName = "rastgeletor"
                debMaintainer = "hllsygn357@hotmail.com"
                menuGroup = "Education"
                appCategory = "Education"
            }

            windows {
                iconFile.set(project.file("src/commonMain/composeResources/drawable/app_icon_win.ico"))
                menuGroup = "Rastgeletor"
                perUserInstall = true
                shortcut = true
            }

            modules("java.sql", "java.naming", "jdk.unsupported")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}
