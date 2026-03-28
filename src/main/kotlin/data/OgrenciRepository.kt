package data

object OgrenciRepository {

    fun tumOgrenciler(): List<Ogrenci> {
        val conn = Database.getConnection()
        val rs = conn.createStatement().executeQuery("SELECT * FROM OGRENCILER")
        val list = mutableListOf<Ogrenci>()
        while (rs.next()) {
            list.add(Ogrenci(rs.getInt("id"), rs.getString("adSoyad"), rs.getString("cinsiyet")))
        }
        return list
    }

    fun ekle(adSoyad: String, cinsiyet: String) {
        val conn = Database.getConnection()
        val ps = conn.prepareStatement("INSERT INTO OGRENCILER (adSoyad, cinsiyet) VALUES (?, ?)")
        ps.setString(1, adSoyad)
        ps.setString(2, cinsiyet)
        ps.executeUpdate()
    }

    fun sil(id: Int) {
        val conn = Database.getConnection()
        val ps = conn.prepareStatement("DELETE FROM OGRENCILER WHERE id = ?")
        ps.setInt(1, id)
        ps.executeUpdate()
    }

    fun tumunuSil() {
        Database.getConnection().createStatement().executeUpdate("DELETE FROM OGRENCILER")
    }

    fun cinsiyeteGore(cinsiyet: String): List<Ogrenci> {
        val conn = Database.getConnection()
        val ps = conn.prepareStatement("SELECT * FROM OGRENCILER WHERE cinsiyet = ?")
        ps.setString(1, cinsiyet)
        val rs = ps.executeQuery()
        val list = mutableListOf<Ogrenci>()
        while (rs.next()) {
            list.add(Ogrenci(rs.getInt("id"), rs.getString("adSoyad"), rs.getString("cinsiyet")))
        }
        return list
    }

    fun karisikListe(cinsiyetModu: String): List<Ogrenci> {
        val liste = when (cinsiyetModu) {
            "erkek" -> cinsiyeteGore("Erkek")
            "kiz" -> cinsiyeteGore("Kız")
            else -> tumOgrenciler()
        }
        return liste.shuffled()
    }

    fun gruplarOlustur(grupSayisiIle: Boolean, deger: Int): List<List<Ogrenci>> {
        val karisik = tumOgrenciler().shuffled().toMutableList()
        if (karisik.isEmpty()) return emptyList()

        return if (grupSayisiIle) {
            val grupSayisi = deger.coerceAtLeast(1)
            val herGruptaki = karisik.size / grupSayisi
            val kalan = karisik.size % grupSayisi
            val gruplar = MutableList(grupSayisi) { mutableListOf<Ogrenci>() }
            var idx = 0
            for (g in 0 until grupSayisi) {
                repeat(herGruptaki) { gruplar[g].add(karisik[idx++]) }
            }
            for (k in 0 until kalan) {
                gruplar[grupSayisi - 1 - k].add(karisik[idx++])
            }
            gruplar
        } else {
            val kisiSayisi = deger.coerceAtLeast(1)
            karisik.chunked(kisiSayisi)
        }
    }
}
