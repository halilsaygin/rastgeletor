package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Ogrenci
import data.OgrenciRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GruplandirmaEkran(onGeri: () -> Unit) {
    var gruplar by remember { mutableStateOf<List<List<Ogrenci>>>(emptyList()) }
    var aktifGrupIndex by remember { mutableStateOf(0) }
    var ayarDialog by remember { mutableStateOf(false) }
    var bilgiMesaj by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFAFAFA))) {
        // Üst Bar (Minimalist tasarım)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = onGeri, 
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("← Geri", color = Color(0xFF666666), fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
            Text(
                text = "Gruplayıcı",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            OutlinedButton(
                onClick = { ayarDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111111)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF111111)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                modifier = Modifier.height(40.dp).align(Alignment.CenterEnd)
            ) {
                Text("Yeni Gruplandırma", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Alt Bölge: İçerik
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Tüm içeriği ortalar
        ) {
            if (gruplar.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Gruplandırma başlatmak için sağ üstten 'Yeni Gruplandırma' seçin.", color = Color.Gray, fontSize = 15.sp)
                }
            } else {
                Text(
                    text = "G R U P   ${aktifGrupIndex + 1}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF666666),
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 24.dp) // Başlık ve kutu arası ideal mesafe
                )

                Row(
                    modifier = Modifier.fillMaxWidth(), // Yüksekliği serbest bırak, sadece genişliği yay
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Sol ok
                    OutlinedButton(
                        onClick = {
                            if (aktifGrupIndex > 0) aktifGrupIndex--
                            else bilgiMesaj = "Bu ilk gruptu :)"
                        },
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111111)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("◀", fontSize = 20.sp) }

                    // Liste
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 120.dp, max = 380.dp), // Kutu yüksekliğini optimum ayarlar, devasa boşluk engellenir
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(gruplar[aktifGrupIndex]) { ogr ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFAFAFA))
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ogr.adSoyad,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF111111)
                                    )
                                }
                            }
                        }
                    }

                    // Sağ ok
                    OutlinedButton(
                        onClick = {
                            if (aktifGrupIndex < gruplar.size - 1) aktifGrupIndex++
                            else bilgiMesaj = "Bu son gruptu :)"
                        },
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111111)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("▶", fontSize = 20.sp) }
                }
            }
        }
    }

    if (ayarDialog) {
        GrupAyarDialog(
            onOnayla = { grupSayisiIle, deger ->
                gruplar = OgrenciRepository.gruplarOlustur(grupSayisiIle, deger)
                aktifGrupIndex = 0
                ayarDialog = false
            },
            onIptal = { ayarDialog = false }
        )
    }

    bilgiMesaj?.let { mesaj ->
        AlertDialog(
            onDismissRequest = { bilgiMesaj = null },
            containerColor = Color.White,
            title = { Text("Bilgi", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A)) },
            text = { Text(mesaj, color = Color(0xFF4A4A4A), fontSize = 15.sp) },
            confirmButton = { 
                OutlinedButton(
                    onClick = { bilgiMesaj = null },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111111)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF111111)),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Tamam") }
            }
        )
    }
}

@Composable
private fun GrupAyarDialog(onOnayla: (Boolean, Int) -> Unit, onIptal: () -> Unit) {
    var grupSayisiIle by remember { mutableStateOf(true) }
    var deger by remember { mutableStateOf("2") }

    AlertDialog(
        onDismissRequest = onIptal,
        containerColor = Color.White,
        title = { Text("Gruplama Ayarları", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { grupSayisiIle = true }
                            .padding(vertical = 2.dp, horizontal = 0.dp)
                    ) {
                        RadioButton(
                            selected = grupSayisiIle, 
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF111111))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Grup sayısı ile", fontSize = 15.sp, color = Color(0xFF111111))
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { grupSayisiIle = false }
                            .padding(vertical = 2.dp, horizontal = 0.dp)
                    ) {
                        RadioButton(
                            selected = !grupSayisiIle, 
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF111111))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gruptaki kişi sayısı ile", fontSize = 15.sp, color = Color(0xFF111111))
                    }
                }
                
                OutlinedTextField(
                    value = deger,
                    onValueChange = { if (it.all(Char::isDigit)) deger = it },
                    label = { Text(if (grupSayisiIle) "Grup Sayısı" else "Kişi Sayısı", color = Color(0xFF666666)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF111111),
                        unfocusedBorderColor = Color(0xFFDDDDDD),
                        cursorColor = Color(0xFF111111)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = { onOnayla(grupSayisiIle, deger.toIntOrNull() ?: 2) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111111)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF111111)),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Uygula") }
        },
        dismissButton = { 
            TextButton(
                onClick = onIptal,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF666666))
            ) { Text("İptal") }
        }
    )
}
