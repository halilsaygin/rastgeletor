# Rastgeletör - Sınıf Yönetim Aracı

Okullarda akıllı tahta üzerinden kullanılmak üzere tasarlanmış, öğretmenlerin sınıf içi aktivitelerini kolaylaştıran bir masaüstü uygulamasıdır.

## 🎯 Özellikler

- **Rastgele Öğrenci Seçimi**: Sınıftan adil ve rastgele öğrenci seçimi
- **Grup Oluşturma**: İstediğiniz grup sayısına veya grup başına düşen kişi sayısına göre otomatik gruplama
- **Cinsiyet Filtresi**: Tüm sınıf, sadece kızlar veya sadece erkekler arasından seçim
- **Liste Modları**: Eksilen liste (seçilen öğrenci tekrar seçilmez) veya sabit liste
- **Öğrenci Yönetimi**: Kolay öğrenci ekleme, silme ve listeleme

## 📥 Kurulum ve Kullanım

### Son Kullanıcılar İçin

1. Projeyi indirin veya klonlayın
2. Java 19 veya üzeri yüklü olduğundan emin olun
3. Komut satırından şu komutu çalıştırın:
   ```bash
   gradle run
   ```
4. Uygulama açıldığında:
   - İlk olarak "Öğrenci" menüsünden "Sınıf Listesi Ayarla" seçeneğine tıklayın
   - Öğrenci adlarını ve cinsiyetlerini ekleyin
   - Ana ekrandan "Rastgeletör" veya "Gruplayıcı" seçeneklerini kullanın

### Geliştiriciler İçin

**Gereksinimler:**
- Java 19+
- Gradle 7.0+

**Projeyi Çalıştırma:**
```bash
# Projeyi derle
gradle build

# Uygulamayı çalıştır
gradle run

# Temizlik
gradle clean
```

**Proje Yapısı:**
```
src/
├── main/
│   ├── java/main/          # Java kaynak kodları
│   │   ├── controllers/    # FXML controller sınıfları
│   │   ├── daos/          # Veritabanı erişim katmanı
│   │   └── services/      # İş mantığı katmanı
│   └── resources/         # FXML, CSS, resim dosyaları
```

## 🎮 Kullanım Kılavuzu

### Öğrenci Listesi Oluşturma
1. Ana ekranda "Öğrenci" menüsünden "Sınıf Listesi Ayarla"yı seçin
2. Öğrenci adını girin, cinsiyeti seçin ve "EKLE" butonuna tıklayın
3. Listeyi tamamladıktan sonra ev ikonuna tıklayarak ana ekrana dönün

### Rastgele Öğrenci Seçme
1. Ana ekranda "🎲 Rastgeletör" butonuna tıklayın
2. "Yeni" menüsünden seçim ayarlarını yapın:
   - Liste modu (Eksilen/Sabit)
   - Cinsiyet filtresi (Tüm Sınıf/Kızlar/Erkekler)
3. Ortadaki 🔄 butonuna tıklayarak rastgele öğrenci seçin

### Grup Oluşturma
1. Ana ekranda "👥 Gruplayıcı" butonuna tıklayın
2. "Yeni" menüsünden "Yeni Gruplandırma"yı seçin
3. Gruplama yöntemini belirleyin:
   - Grup sayısı ile (örn: 5 grup)
   - Gruptaki kişi sayısı ile (örn: her grupta 4 kişi)
4. ◀ ▶ butonlarıyla gruplar arasında gezinin

## 🗄️ Veritabanı

Uygulama SQLite kullanır. Öğrenci verileri `ogrenciler.db` dosyasında saklanır ve otomatik olarak oluşturulur.

## 📝 Lisans

Bu proje eğitim amaçlı geliştirilmiştir.

## 🤝 Katkıda Bulunma

Projeyi fork'layıp geliştirmeler yapabilir, pull request gönderebilirsiniz.
