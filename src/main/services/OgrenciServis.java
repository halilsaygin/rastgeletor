package main.services;

import java.sql.SQLException;
import java.util.Collections;
import javafx.collections.ObservableList;
import main.Ogrenci;
import main.daos.OgrenciDAO;

public class OgrenciServis {
        private final OgrenciDAO ogrenciDAO;

       public OgrenciServis(OgrenciDAO ogrenciDAO) {
           this.ogrenciDAO = ogrenciDAO;
       }

       public void ogrenciEkle(String adSoyad, String cinsiyet) throws SQLException {
            ogrenciDAO.ogrenciEkle(adSoyad, cinsiyet);
       }
       
       public void ogrenciSil(int id) throws SQLException{
            ogrenciDAO.ogrenciSil(id);
       }

       public ObservableList<Ogrenci> tumOgrencileriGetir() throws SQLException {
            return ogrenciDAO.ogrenciListesiGetir();
       }

       public ObservableList<Ogrenci> rastgeleGruplarOlustur(int grupSayisi) throws SQLException {
            ObservableList<Ogrenci> ogrenciler = ogrenciDAO.ogrenciListesiGetir();
            // Gruplama algoritması burada uygulanabilir
            return ogrenciler;
       }

       public Ogrenci rastgeleOgrenciGetir() throws SQLException{
            ObservableList<Ogrenci> ogrenciler = ogrenciDAO.ogrenciListesiGetir();
            if (ogrenciler.isEmpty()) {
                return null; // Liste boşsa null döndür
            }
            Collections.shuffle(ogrenciler);
            return ogrenciler.get(0); // Rastgele bir öğrenci
   }

   public ObservableList<ObservableList<Ogrenci>> gruplaraAyir(int grupSayisi) throws SQLException {
       ObservableList<Ogrenci> ogrenciler = ogrenciDAO.ogrenciListesiGetir();
       Collections.shuffle(ogrenciler); // Karıştır
       // Gruplama işlemi
            return null;
   }
    
    
}
