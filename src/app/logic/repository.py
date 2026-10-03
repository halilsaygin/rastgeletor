import random
from typing import List
from .models import Ogrenci
from .database import Database


class OgrenciRepository:

    @classmethod
    def tum_ogrenciler(cls) -> List[Ogrenci]:
        conn = Database.get_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT id, adSoyad, cinsiyet FROM OGRENCILER ORDER BY id ASC")
        rows = cursor.fetchall()
        return [Ogrenci(id=row["id"], ad_soyad=row["adSoyad"], cinsiyet=row["cinsiyet"]) for row in rows]

    @classmethod
    def ekle(cls, ad_soyad: str, cinsiyet: str) -> None:
        conn = Database.get_connection()
        cursor = conn.cursor()
        cursor.execute("INSERT INTO OGRENCILER (adSoyad, cinsiyet) VALUES (?, ?)", (ad_soyad.strip(), cinsiyet))

    @classmethod
    def sil(cls, ogrenci_id: int) -> None:
        conn = Database.get_connection()
        cursor = conn.cursor()
        cursor.execute("DELETE FROM OGRENCILER WHERE id = ?", (ogrenci_id,))

    @classmethod
    def tumunu_sil(cls) -> None:
        conn = Database.get_connection()
        cursor = conn.cursor()
        cursor.execute("DELETE FROM OGRENCILER")

    @classmethod
    def cinsiyete_gore(cls, cinsiyet: str) -> List[Ogrenci]:
        conn = Database.get_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT id, adSoyad, cinsiyet FROM OGRENCILER WHERE cinsiyet = ? ORDER BY id ASC", (cinsiyet,))
        rows = cursor.fetchall()
        return [Ogrenci(id=row["id"], ad_soyad=row["adSoyad"], cinsiyet=row["cinsiyet"]) for row in rows]

    @classmethod
    def karisik_liste(cls, cinsiyet_modu: str) -> List[Ogrenci]:
        if cinsiyet_modu == "erkek":
            liste = cls.cinsiyete_gore("Erkek")
        elif cinsiyet_modu == "kiz":
            liste = cls.cinsiyete_gore("Kız")
        else:
            liste = cls.tum_ogrenciler()

        karisik = list(liste)
        random.shuffle(karisik)
        return karisik

    @classmethod
    def gruplar_olustur(cls, grup_sayisi_ile: bool, deger: int) -> List[List[Ogrenci]]:
        karisik = cls.tum_ogrenciler()
        random.shuffle(karisik)
        if not karisik:
            return []

        if grup_sayisi_ile:
            grup_sayisi = max(1, deger)
            her_gruptaki = len(karisik) // grup_sayisi
            kalan = len(karisik) % grup_sayisi

            gruplar: List[List[Ogrenci]] = [[] for _ in range(grup_sayisi)]
            idx = 0
            for g in range(grup_sayisi):
                for _ in range(her_gruptaki):
                    gruplar[g].append(karisik[idx])
                    idx += 1

            for k in range(kalan):
                gruplar[grup_sayisi - 1 - k].append(karisik[idx])
                idx += 1

            return gruplar
        else:
            kisi_sayisi = max(1, deger)
            return [karisik[i:i + kisi_sayisi] for i in range(0, len(karisik), kisi_sayisi)]

    # CamelCase compatibility aliases
    tumOgrenciler = tum_ogrenciler
    tumunuSil = tumunu_sil
    cinsiyeteGore = cinsiyete_gore
    karisikListe = karisik_liste
    gruplarOlustur = gruplar_olustur
