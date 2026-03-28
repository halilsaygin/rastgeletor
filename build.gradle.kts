import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.1.20"
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

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.components.resources)
    implementation("org.xerial:sqlite-jdbc:3.46.1.0")
}

compose.desktop {
    application {
        mainClass = "MainKt"

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
                iconFile.set(project.file("src/main/resources/app_icon.png"))
                packageName = "rastgeletor"
                debMaintainer = "hllsygn357@hotmail.com"
                menuGroup = "Education"
                appCategory = "Education"
            }

            windows {
                iconFile.set(project.file("src/main/resources/app_icon.ico"))
                menuGroup = "Rastgeletor"
                perUserInstall = true
                shortcut = true
            }

            modules("java.sql", "java.naming", "jdk.unsupported")
        }
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
