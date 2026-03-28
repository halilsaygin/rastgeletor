package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Ogrenci
import data.OgrenciRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OgrenciListEkran(onGeri: () -> Unit) {
    var adSoyad by remember { mutableStateOf("") }
    var cinsiyet by remember { mutableStateOf("Erkek") }
    var ogrenciler by remember { mutableStateOf(OgrenciRepository.tumOgrenciler()) }
    var secilenOgrenci by remember { mutableStateOf<Ogrenci?>(null) }
    var silOnayDialog by remember { mutableStateOf(false) }
    var tumunuSilDialog by remember { mutableStateOf(false) }
    var uyariMesaj by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFAFAFA))) {
        // Üst Bar
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
                text = "Sınıf Listesi",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }

        // İçerik
        Row(
            modifier = Modifier.weight(1f).padding(horizontal = 24.dp, vertical = 8.dp), 
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Sol panel: ekleme formu
            Surface(
                modifier = Modifier.width(300.dp).fillMaxHeight(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Üst Kısım: Form
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text("Yeni Öğrenci", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111111))
                        
                        OutlinedTextField(
                            value = adSoyad,
                            onValueChange = { adSoyad = it },
                            placeholder = { Text("Ad Soyad", color = Color(0xFF999999)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF111111),
                                unfocusedBorderColor = Color(0xFFDDDDDD),
                                cursorColor = Color(0xFF111111)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { cinsiyet = "Erkek" }
                                    .padding(end = 8.dp)
                            ) {
                                RadioButton(
                                    selected = cinsiyet == "Erkek", 
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF111111))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Erkek", fontSize = 15.sp, color = Color(0xFF111111))
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { cinsiyet = "Kız" }
                                    .padding(end = 8.dp)
                            ) {
                                RadioButton(
                                    selected = cinsiyet == "Kız", 
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF111111))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Kız", fontSize = 15.sp, color = Color(0xFF111111))
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF111111), RoundedCornerShape(8.dp))
                                .clickable {
                                    if (adSoyad.isBlank()) {
                                        uyariMesaj = "Ad Soyad boş olamaz!"
                                    } else {
                                        OgrenciRepository.ekle(adSoyad.trim(), cinsiyet)
                                        ogrenciler = OgrenciRepository.tumOgrenciler()
                                        adSoyad = ""
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("EKLE", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111111))
                        }
                    }

                    // Alt Kısım: Sil Butonu
                    if (secilenOgrenci != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFEF9A9A), RoundedCornerShape(8.dp))
                                .clickable { silOnayDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Seçili Öğrenciyi Sil", fontSize = 14.sp, color = Color(0xFFD32F2F))
                        }
                    } else {
                        // Seçili öğrenci yokken sil butonunun kapladığı yüksekliği boş olarak korumak
                        // arayüz zıplamalarını engeller. Spacer ile alanı sabit tutuyoruz.
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }

            // Sağ panel: liste
            Surface(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFAFAFA))
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Text("AD SOYAD", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF666666), letterSpacing = 1.sp)
                        Text("CİNSİYET", modifier = Modifier.width(80.dp), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF666666), letterSpacing = 1.sp)
                    }
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                        items(ogrenciler) { ogr ->
                            val secili = secilenOgrenci?.id == ogr.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (secili) Color(0xFFF2F2F2) else Color.Transparent)
                                    .clickable { secilenOgrenci = if (secili) null else ogr }
                                    .padding(horizontal = 20.dp, vertical = 16.dp)
                            ) {
                                Text(
                                    text = ogr.adSoyad, 
                                    modifier = Modifier.weight(1f), 
                                    fontSize = 15.sp, 
                                    color = if (secili) Color.Black else Color(0xFF333333),
                                    fontWeight = if (secili) FontWeight.Medium else FontWeight.Normal
                                )
                                Text(
                                    text = ogr.cinsiyet, 
                                    modifier = Modifier.width(80.dp), 
                                    fontSize = 15.sp, 
                                    color = if (secili) Color.Black else Color(0xFF666666)
                                )
                            }
                            HorizontalDivider(color = Color(0xFFF5F5F5))
                        }
                    }
                }
            }
        }

        // Alt: Tüm Kayıtları Sil butonu
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { tumunuSilDialog = true },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF666666))
            ) {
                Text("Tüm Kayıtları Sıfırla", fontSize = 13.sp)
            }
        }
    }

    // Dialog components
    if (silOnayDialog) {
        AlertDialog(
            onDismissRequest = { silOnayDialog = false },
            containerColor = Color.White,
            title = { Text("Silme Onayı", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A)) },
            text = { Text("${secilenOgrenci?.adSoyad} isimli öğrenci silinecek. Onaylıyor musunuz?", color = Color(0xFF4A4A4A)) },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        secilenOgrenci?.let { OgrenciRepository.sil(it.id) }
                        ogrenciler = OgrenciRepository.tumOgrenciler()
                        secilenOgrenci = null
                        silOnayDialog = false
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF9A9A))
                ) { Text("Evet, Sil") }
            },
            dismissButton = { TextButton(onClick = { silOnayDialog = false }, colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF666666))) { Text("İptal") } }
        )
    }

    if (tumunuSilDialog) {
        AlertDialog(
            onDismissRequest = { tumunuSilDialog = false },
            containerColor = Color.White,
            title = { Text("Tümünü Sil Onayı", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A)) },
            text = { Text("Listeyi tamamen temizlemek üzeresiniz. Bu işlem geri alınamaz. Devam edilsin mi?", color = Color(0xFF4A4A4A)) },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        OgrenciRepository.tumunuSil()
                        ogrenciler = OgrenciRepository.tumOgrenciler()
                        secilenOgrenci = null
                        tumunuSilDialog = false
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF9A9A))
                ) { Text("Hepsini Sil") }
            },
            dismissButton = { TextButton(onClick = { tumunuSilDialog = false }, colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF666666))) { Text("İptal") } }
        )
    }

    uyariMesaj?.let { mesaj ->
        AlertDialog(
            onDismissRequest = { uyariMesaj = null },
            containerColor = Color.White,
            title = { Text("Uyarı", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = { Text(mesaj) },
            confirmButton = { 
                OutlinedButton(
                    onClick = { uyariMesaj = null },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111111)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF111111))
                ) { Text("Tamam") }
            }
        )
    }
}
