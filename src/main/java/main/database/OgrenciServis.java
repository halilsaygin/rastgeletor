package main.database;

import java.sql.SQLException;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.database.Ogrenci;
import main.database.OgrenciDAO;

public class OgrenciServis {

    private final OgrenciDAO ogrenciDAO;
    public ObservableList<ObservableList<Ogrenci>>  ogrenciGruplar;

    public OgrenciServis(OgrenciDAO ogrenciDAO) {
        this.ogrenciDAO = ogrenciDAO;
        this.ogrenciGruplar = FXCollections.observableArrayList();
    }

    public void ogrenciEkle(String adSoyad, String cinsiyet) throws SQLException {
        ogrenciDAO.ogrenciEkle(adSoyad, cinsiyet);
    }

    public void ogrenciSil(int id) throws SQLException {
        ogrenciDAO.ogrenciSil(id);
    }
    
    public void tumOgrenciKayitlariSil() throws SQLException{
        ogrenciDAO.tumKayitlariSil();
    }

    public ObservableList<Ogrenci> tumOgrencileriGetir() throws SQLException {
        return ogrenciDAO.ogrenciListesiGetir();
    }

    public ObservableList<Ogrenci> karisikOgrenciListesiGetir(String cinsiyet_modu) throws SQLException {
        ObservableList<Ogrenci> ogrenciler = FXCollections.observableArrayList();

        // cinsiyete göre liste
        if (cinsiyet_modu.equals("tamami")) {
            ogrenciler = ogrenciDAO.ogrenciListesiGetir();
        } else if (cinsiyet_modu.equals("erkek")) {
            ogrenciler = ogrenciDAO.erkek_ogrenciListesiGetir();
        } else {
            ogrenciler = ogrenciDAO.kiz_ogrenciListesiGetir();
        }
        
        Collections.shuffle(ogrenciler);
        return ogrenciler;
    }

    // GRUPLAMA 
    public ObservableList<ObservableList<Ogrenci>> rastgeleGruplarOlustur(boolean grupSayisi_ile, int grupData) throws SQLException {
        
        if (grupSayisi_ile){ // grup sayısı ile oluşturulacak grupData -> grupSayısı dır
            ogrenciGruplar = grupSayisi_ileAyir(grupData);
        }else{ // gruptaki kişi sayısı ile oluşturulacak. grupData -> grupKisiSayisi dır
            ogrenciGruplar = grupKisiSayisi_ileAyir(grupData);
        }
        return ogrenciGruplar;
    }

    public ObservableList<ObservableList<Ogrenci>> grupSayisi_ileAyir(int grupSayisi) throws SQLException {
        ObservableList<Ogrenci> ogrenciler = ogrenciDAO.ogrenciListesiGetir();
        ObservableList<ObservableList<Ogrenci>> gruplar = FXCollections.observableArrayList();
        Collections.shuffle(ogrenciler); // Karıştır
        
        int herGruptakiKisiSayisi = ogrenciler.size() / grupSayisi; System.out.println("herGruptakiKisiSayisi: "+ herGruptakiKisiSayisi);
        int kalanKisiSayisi =  ogrenciler.size() % grupSayisi;  System.out.println("kalanKisiSayisi: " + kalanKisiSayisi);
        
        for (int grupNo = 1; grupNo <= grupSayisi; grupNo++) {
            ObservableList<Ogrenci> grup = FXCollections.observableArrayList();
            for (int ogrenciSayisi = 1; ogrenciSayisi <= herGruptakiKisiSayisi ; ogrenciSayisi++){
                grup.add(ogrenciler.remove(0));
            }
            gruplar.add(grup);
        }
        
        if (kalanKisiSayisi != 0){ // fazla kaplan kişileri sondaki gruptan başlayarak geriye doğru ekle.
            for (int sonKalanKisiSayisi = 1; sonKalanKisiSayisi <= kalanKisiSayisi ; sonKalanKisiSayisi++) {
                gruplar.get(gruplar.size()-sonKalanKisiSayisi).add(ogrenciler.remove(0));
            }
        }
        return gruplar;
    }
    
    public ObservableList<ObservableList<Ogrenci>> grupKisiSayisi_ileAyir(int grupKisiSayisi) throws SQLException {
        ObservableList<Ogrenci> ogrenciler = ogrenciDAO.ogrenciListesiGetir();
        ObservableList<ObservableList<Ogrenci>> gruplar = FXCollections.observableArrayList();

        Collections.shuffle(ogrenciler); // Öğrencileri karıştır

        // Yeni bir grup oluştur
        ObservableList<Ogrenci> grup = FXCollections.observableArrayList();

        // Öğrencileri gruplara ayır
        for (Ogrenci ogrenci : ogrenciler) {
            grup.add(ogrenci);
            if (grup.size() == grupKisiSayisi) {
                gruplar.add(grup);
                grup = FXCollections.observableArrayList(); // Yeni bir grup oluştur
            }
        }

        // Son grupta eksik öğrenci kalmışsa onu da ekle
        if (!grup.isEmpty()) {
            gruplar.add(grup);
        }

        return gruplar;
    }


}
