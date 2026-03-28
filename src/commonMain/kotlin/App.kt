import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import ui.*

@Composable
fun App() {
    var ekran by remember { mutableStateOf<Screen>(Screen.Rastgeletor) }

    AnimatedContent(
        targetState = ekran,
        transitionSpec = {
            if (targetState != Screen.Rastgeletor && initialState == Screen.Rastgeletor) {
                // Rastgeletor -> Diğer (Sağdan Sola)
                slideInHorizontally(animationSpec = tween(300)) { it } togetherWith 
                slideOutHorizontally(animationSpec = tween(300)) { -it }
            } else {
                // Diğer -> Rastgeletor (Soldan Sağa)
                slideInHorizontally(animationSpec = tween(300)) { -it } togetherWith 
                slideOutHorizontally(animationSpec = tween(300)) { it }
            }
        },
        label = "ekran_gecisi"
    ) { hedefEkran ->
        when (hedefEkran) {
            is Screen.OgrenciList -> OgrenciListEkran(
                onGeri = { ekran = Screen.Rastgeletor }
            )
            is Screen.Rastgeletor -> RastgeletorEkran(
                onNavigate = { ekran = it }
            )
            is Screen.Gruplandirma -> GruplandirmaEkran(
                onGeri = { ekran = Screen.Rastgeletor }
            )
        }
    }
}
