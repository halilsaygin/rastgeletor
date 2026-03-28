import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Taskbar
import java.awt.desktop.UserSessionEvent

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

fun main() = application {
    // Set application name for Linux window managers and taskbar
    System.setProperty("apple.awt.application.name", "Rastgeletor")
    System.setProperty("awt.useSystemAAFontSettings", "on")
    // This helps some Linux WMs to correctly identify the app name and class
    System.setProperty("sun.java2d.wm.classname", "Rastgeletor")
    System.setProperty("sun.awt.wm.classname", "Rastgeletor")
    
    // Try to set the application name via Toolkit if available
    try {
        val toolkit = java.awt.Toolkit.getDefaultToolkit()
        val desktop = java.awt.Desktop.getDesktop()
        // This helps with some Linux WMs
        System.setProperty("java.awt.headless", "false")
    } catch (e: Exception) {
        // Ignore if not available
    }
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "Rastgeletör",
        icon = BitmapPainter(useResource("app_icon.png", ::loadImageBitmap)),
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
