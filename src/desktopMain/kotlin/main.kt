@file:JvmName("Rastgeletor")

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource
import com.rastgeletor.Res
import com.rastgeletor.app_icon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.File
import java.io.PrintStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

private val AppColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    secondary = Color.DarkGray,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color.Black,
    outline = Color(0xFFCCCCCC),
    primaryContainer = Color(0xFFE0E0E0),
    onPrimaryContainer = Color.Black,
    errorContainer = Color(0xFFFFE0E0),
    onErrorContainer = Color(0xFF8B0000)
)

/** Uygulama log dizinini döner ve oluşturur. */
fun getLogDir(): File {
    val userHome = System.getProperty("user.home")
    val logDir = File(
        System.getenv("XDG_DATA_HOME") ?: "$userHome/.local/share",
        "Rastgeletor/logs"
    )
    logDir.mkdirs()
    return logDir
}

/** Dosya tabanlı loglama sistemini başlatır. stdout/stderr de dosyaya yönlendirilir. */
fun setupLogging(): Logger {
    val logDir = getLogDir()
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
    val logFile = File(logDir, "rastgeletor_$timestamp.log")

    // java.util.logging ile dosya logger
    val logger = Logger.getLogger("Rastgeletor")
    logger.level = Level.ALL

    val fileHandler = FileHandler(logFile.absolutePath, true)
    fileHandler.formatter = SimpleFormatter()
    fileHandler.level = Level.ALL
    logger.addHandler(fileHandler)
    logger.useParentHandlers = false

    // stdout ve stderr'i de aynı dosyaya yönlendir
    val logStream = PrintStream(logFile.outputStream().also {}, true, "UTF-8")
    System.setOut(logStream)
    System.setErr(logStream)

    logger.info("=== Rastgeletor başlatıldı: $timestamp ===")
    logger.info("OS: ${System.getProperty("os.name")} ${System.getProperty("os.version")}")
    logger.info("JVM: ${System.getProperty("java.version")} (${System.getProperty("java.vendor")})")
    logger.info("Arch: ${System.getProperty("os.arch")}")
    logger.info("Log dosyası: ${logFile.absolutePath}")

    // DISPLAY ve Wayland ortam değişkenlerini logla
    logger.info("DISPLAY: ${System.getenv("DISPLAY") ?: "(yok)"}")
    logger.info("WAYLAND_DISPLAY: ${System.getenv("WAYLAND_DISPLAY") ?: "(yok)"}")
    logger.info("XDG_SESSION_TYPE: ${System.getenv("XDG_SESSION_TYPE") ?: "(yok)"}")
    logger.info("XDG_CURRENT_DESKTOP: ${System.getenv("XDG_CURRENT_DESKTOP") ?: "(yok)"}")
    logger.info("GTK_THEME: ${System.getenv("GTK_THEME") ?: "(yok)"}")
    logger.info("JAVA_HOME: ${System.getProperty("java.home")}")

    // Yakalanmamış exception'ları logla
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        logger.severe("Yakalanmamış hata [${thread.name}]: ${throwable.message}")
        throwable.printStackTrace(logStream)
    }

    return logger
}

fun setupLinuxEnvironment(logger: Logger) {
    val os = System.getProperty("os.name").lowercase()
    if (!os.contains("linux")) return

    logger.info("Linux/Cinnamon (X11) ortamı ayarlanıyor...")

    // Font anti-aliasing — Cinnamon varsayılanıyla uyumlu
    System.setProperty("awt.useSystemAAFontSettings", "lcd")
    System.setProperty("swing.aatext", "true")

    // WM class — Cinnamon panel ve görev yöneticisi için
    System.setProperty("sun.java2d.wm.classname", "Rastgeletor")
    System.setProperty("awt.appClassName", "Rastgeletor")

    // XRender — Cinnamon/X11 için kararlı seçenek
    System.setProperty("sun.java2d.xrender", "true")
    System.setProperty("sun.java2d.opengl", "false")
    System.setProperty("java.awt.headless", "false")

    logger.info("DISPLAY: ${System.getenv("DISPLAY") ?: "(yok)"}")
    logger.info("XDG_CURRENT_DESKTOP: ${System.getenv("XDG_CURRENT_DESKTOP") ?: "(yok)"}")
    logger.info("Linux ortam ayarları uygulandı.")
}

fun main() {
    val logger = setupLogging()

    try {
        setupLinuxEnvironment(logger)

        logger.info("Compose Desktop penceresi açılıyor...")

        application {
            Window(
                onCloseRequest = {
                    logger.info("Uygulama kapatılıyor.")
                    exitApplication()
                },
                title = "Rastgeletör",
                icon = painterResource(Res.drawable.app_icon),
                state = rememberWindowState(
                    width = 750.dp,
                    height = 600.dp,
                    position = WindowPosition(Alignment.Center)
                ),
                resizable = false,
                visible = true
            ) {
                MaterialTheme(colorScheme = AppColorScheme) {
                    App()
                }
            }
        }

    } catch (e: Exception) {
        logger.severe("Kritik hata: ${e.message}")
        e.printStackTrace()
        throw e
    }
}
