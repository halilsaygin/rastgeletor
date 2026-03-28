package ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.focusable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.OgrenciRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RastgeletorEkran(onNavigate: (Screen) -> Unit) {
    var listeModu by remember { mutableStateOf("eksilen_liste") }
    var cinsiyetModu by remember { mutableStateOf("tamami") }
    var ogrenciler by remember { mutableStateOf(OgrenciRepository.karisikListe(cinsiyetModu)) }
    var secilenIsim by remember { mutableStateOf("Seçim Bekleniyor") }
    var ayarDialog by remember { mutableStateOf(false) }
    var oncekiIndeks by remember { mutableStateOf(-1) }

    var butonBasildi by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (butonBasildi) 0.9f else 1f,
        animationSpec = tween(80),
        finishedListener = { butonBasildi = false }
    )

    fun sec() {
        butonBasildi = true
        if (ogrenciler.isNotEmpty()) {
            if (listeModu == "eksilen_liste") {
                val mutable = ogrenciler.toMutableList()
                secilenIsim = mutable.removeAt(0).adSoyad
                ogrenciler = mutable
            } else {
                var idx: Int
                do { idx = ogrenciler.indices.random() } while (idx == oncekiIndeks && ogrenciler.size > 1)
                secilenIsim = ogrenciler[idx].adSoyad
                oncekiIndeks = idx
            }
        } else {
            secilenIsim = "LİSTE BOŞ"
        }
    }

    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown &&
                    (event.key == Key.Enter || event.key == Key.NumPadEnter)
                ) {
                    sec(); true
                } else false
            }
    ) {
        // Üst Bar (Minimalist tasarım)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rastgeletör",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(
                    onClick = { ayarDialog = true },
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text("Seçim Ayarları", color = Color(0xFF4A4A4A), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                OutlinedButton(
                    onClick = { onNavigate(Screen.OgrenciList) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1A1A1A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Öğrenci Listesi", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                OutlinedButton(
                    onClick = { onNavigate(Screen.Gruplandirma) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF1A1A1A)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Gruplayıcı", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.8f))

        // Seçilen İsim Bloğu
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "S E Ç İ L E N   Ö Ğ R E N C İ",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF666666),
                letterSpacing = 2.sp
            )

            Text(
                text = secilenIsim,
                fontSize = 64.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF111111),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp),
                lineHeight = 72.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Büyük siyah zar ikonu (Arkaplansız, bordersız, siyah forecolor)
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(100.dp)
                .scale(scale)
                .clip(CircleShape)
                .clickable { sec() },
            contentAlignment = Alignment.Center
        ) {
            Text("⚄", fontSize = 72.sp, color = Color(0xFF111111), modifier = Modifier.offset(y = (-4).dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Alt Bilgi Çubuğu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val modText = if (listeModu == "eksilen_liste") "Eksilen Liste" else "Sabit Liste"
            val cinsiyetText = when(cinsiyetModu) { "erkek" -> "Sadece Erkekler"; "kiz" -> "Sadece Kızlar"; else -> "Tüm Sınıf" }
            val kalanMetin = if (listeModu == "eksilen_liste") "  •  Kalan: ${ogrenciler.size}" else ""
            
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFEEEEEE),
            ) {
                Text(
                    text = "$modText  •  $cinsiyetText$kalanMetin",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
    }

    if (ayarDialog) {
        RastgeletorAyarDialog(
            mevcutListeModu = listeModu,
            mevcutCinsiyetModu = cinsiyetModu,
            onOnayla = { yeniListeModu, yeniCinsiyetModu ->
                listeModu = yeniListeModu
                cinsiyetModu = yeniCinsiyetModu
                ogrenciler = OgrenciRepository.karisikListe(cinsiyetModu)
                oncekiIndeks = -1
                secilenIsim = "Seçim Bekleniyor"
                ayarDialog = false
            },
            onIptal = { ayarDialog = false }
        )
    }
}

@Composable
private fun RastgeletorAyarDialog(
    mevcutListeModu: String,
    mevcutCinsiyetModu: String,
    onOnayla: (String, String) -> Unit,
    onIptal: () -> Unit
) {
    var listeModu by remember { mutableStateOf(mevcutListeModu) }
    var cinsiyetModu by remember { mutableStateOf(mevcutCinsiyetModu) }

    AlertDialog(
        onDismissRequest = onIptal,
        containerColor = Color.White,
        title = {
            Text(
                "Seçim Ayarları",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text("Liste Modu", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF888888), modifier = Modifier.padding(bottom = 4.dp))
                    listOf("eksilen_liste" to "Eksilen Liste", "sabit_liste" to "Sabit Liste").forEach { (mod, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { listeModu = mod }
                                .padding(vertical = 2.dp, horizontal = 0.dp)
                        ) {
                            RadioButton(
                                selected = listeModu == mod,
                                onClick = null, // Row'un clickable'ı tıklamayı yönetecek, böylece fazladan boşluk oluşmaz
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF111111))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, fontSize = 15.sp, color = Color(0xFF111111))
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFEEEEEE))

                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text("Cinsiyet Filtresi", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF888888), modifier = Modifier.padding(bottom = 4.dp))
                    listOf("tamami" to "Tüm Sınıf", "erkek" to "Sadece Erkekler", "kiz" to "Sadece Kızlar").forEach { (mod, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { cinsiyetModu = mod }
                                .padding(vertical = 2.dp, horizontal = 0.dp)
                        ) {
                            RadioButton(
                                selected = cinsiyetModu == mod,
                                onClick = null, // Row üzerinden tıklanır, boşluklar kaybolur
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF111111))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, fontSize = 15.sp, color = Color(0xFF111111))
                        }
                    }
                }
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = { onOnayla(listeModu, cinsiyetModu) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111111)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF111111)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Text("Uygula", fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onIptal,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF666666))
            ) {
                Text("İptal", fontWeight = FontWeight.Medium)
            }
        }
    )
}
