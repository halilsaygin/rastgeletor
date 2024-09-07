/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package main.controllers;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import main.DatabaseOperations;
import main.Ogrenci;

public class FXML_OgrenciSecController implements Initializable {
    String[] cinsiyet = {"Kız","Erkek"};
    public String secilenCinsiyet;
    public String secimMod;
    public DatabaseOperations database;
    ObservableList<Ogrenci> ogrenciList;
    
     @FXML
    public StackPane stackpane;
    public MenuItem mnItem_eksilen_liste, mnItem_sabit_liste;
    public Pane pn_sec,pn_sinif_liste;
    //pn_sec
    public Button btn_sec;
    public Label secilen_isim;
    public RadioButton rdbtn_tum_sinif,rdbtn_sadece_kizlar,rdbtn_sadece_erkekler;
    // pn_sinif_liste
    // Sütunları tanımlama
    public TableColumn<Ogrenci, Integer> idSutunu;
    public TableColumn<Ogrenci, String> adSoyadSutunu;
    public TableColumn<Ogrenci, String> cinsiyetSutunu;
    public ChoiceBox<String> chbx_cinsiyet;
    public Button btn_ogrenci_ekle;
    public TextField txtf_ad_soyad;
    public TableView tblview_ogrenci_liste;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Sütunları başlat
        idSutunu.setCellValueFactory(new PropertyValueFactory<>("id"));
        adSoyadSutunu.setCellValueFactory(new PropertyValueFactory<>("adSoyad"));
        cinsiyetSutunu.setCellValueFactory(new PropertyValueFactory<>("cinsiyet"));
        
        database = new DatabaseOperations();// Database başlat
        ogrenciList = database.ogrenciListesiGetir(); // tableview dinamik listesini veritabanından al.
        
        tblview_ogrenci_liste.setItems(database.ogrenciListesiGetir()); // tableview listesini veritabanından al
        
        secilenCinsiyet = "tamami";
        secimMod = "eksilen";
        pn_sec.setVisible(true); // seçme ekranır görünür
        pn_sinif_liste.setVisible(false); // liste ekaranı görünmez.
        chbx_cinsiyet.getItems().addAll(cinsiyet); // cinsiyet seçme choicebox veri grubu atama
    }
    
    @FXML
    //seçilen öğrenci label'a yazdır
    public void sec_ogrenci() {
        btnAnimation(btn_sec);
        if(!database.ogrenciListesiGetir().isEmpty()){
            secilen_isim.setText(rastgeleIsımSec());
        }else{
            bos_OgrenciListe_uyari();
        } 
    }
    
    //veri tabanı ve tableview içine öğrenci ekler
    public void ekle_ogrenci(){
        btnAnimation(btn_ogrenci_ekle);
        // butona basılıp tetiklendiğinde veritabanına öğrenci eklenir ve 
        // tblview_ogrenci_liste adındaki tableview içinde öğrenci gösterilir.
        String ogrenci_adSoyad = txtf_ad_soyad.getText();
        String ogrenci_cinsiyet = chbx_cinsiyet.getValue();
        
        if (ogrenci_adSoyad.isEmpty() || ogrenci_cinsiyet == null){
             JOptionPane.showMessageDialog(null,"Verileriniz Eksik!","UYARI",JOptionPane.WARNING_MESSAGE);
             return;
        }
        
        //Veritabanına öğrenci ekle
        database.ogrenciEkle(ogrenci_adSoyad,ogrenci_cinsiyet);
        
        // TableView'da öğrenci göster.
        int id = database.ogrenciIdGetir(ogrenci_adSoyad);
        
        Ogrenci ogrenci = new Ogrenci(new SimpleIntegerProperty(id),new SimpleStringProperty(ogrenci_adSoyad),
                new SimpleStringProperty(ogrenci_cinsiyet));
        
        if (id > 0) {
            ogrenciList.add(ogrenci);//listeye öğrenci ekle.
            // TableView'ı güncelleme
            tblview_ogrenci_liste.setItems(database.ogrenciListesiGetir());
           
            txtf_ad_soyad.clear();  // ekle textfield içini temizle
            chbx_cinsiyet.setValue(null); // choicebox sıfırla
            
        } else {
            // Hata mesajı gösterme
           JOptionPane.showMessageDialog(null,"Öğrenci EKLENEMEDİ!","HATA",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //Yeni seçme listesi başlatır.
    public void liste_baslat(ActionEvent event){
        makeVisible("secme ekrani");
         // Tıklanan düğmeyi al
        MenuItem menuItem = (MenuItem) event.getSource();
        // Veri tabanında kayıt yoksa uyar ve sınıf listesi ekranına atla
        if(!database.ogrenciListesiGetir().isEmpty()){
            // ID'sini kontrol et
            if ("mnItem_eksilen_liste".equals(menuItem.getId())) {
                // eksilen liste tıklandı
                secimMod = "eksilen";
                System.out.println("eksilen liste tıklandı");
            } else if ("mnItem_sabit_liste".equals(menuItem.getId())) {
                // sabit liste tıklandı
                secimMod = "sabit";
                System.out.println("sabit liste tıklandı");
            }
            
            //seçilen cinsiyete göre ogrenciList listesini veritabanından günceller.
            switch (secilenCinsiyet) {
                case "kizlar" : ogrenciList = database.ogrenciListesiGetir("Kız"); break;
                case "erkekler": ogrenciList = database.ogrenciListesiGetir("Erkek"); break;
                case "tamami":ogrenciList = database.ogrenciListesiGetir(); break;
            }
            secilen_isim.setText("Kimse seçilmedi.");
            String yeniTurMesaj = "Yeni Seçim Turu Oluşturuldu.\n" + "Seçim Modu= "
                    + secimMod + "\n" + "Grup Modu= " + secilenCinsiyet;
            JOptionPane.showMessageDialog(null,yeniTurMesaj,"BİLGİ",JOptionPane.INFORMATION_MESSAGE);  
        }else{
            bos_OgrenciListe_uyari();
        }
    }
    
    //Öğrenci listesinden rastgele isim seçip döndürür
    public String rastgeleIsımSec(){
        Random random = new Random();
        int boyut = ogrenciList.size();
        if (boyut==0){
            return "Liste bitti!";
        }
        int indeks = random.nextInt(boyut);
        if (secimMod.equals("eksilen")){
              return ogrenciList.remove(indeks).adSoyadProperty().get();
        }else{
            return ogrenciList.get(indeks).adSoyadProperty().get();
        }
    }
    
    // radiobutton'dan seçilen cinsiyeti alır ve ayarlar
    public void ayarla_cinsiyet(){
        if (rdbtn_tum_sinif.isSelected()) secilenCinsiyet = "tamami";
        else if (rdbtn_sadece_kizlar.isSelected()) secilenCinsiyet = "kizlar";
        else secilenCinsiyet = "erkekler";
    }
    
    // Veritabanı ve tablo içini temizler.
    public void liste_temizle(){
        int secim = JOptionPane.showConfirmDialog(null, 
            "Tüm öğrenci kayıtları silinecek. Emin misiniz?", 
            "TEYİT", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.INFORMATION_MESSAGE);

        if (secim == JOptionPane.YES_OPTION) {
            database.tumKayitlariSil();
            ogrenciList.clear();
            tblview_ogrenci_liste.setItems(database.ogrenciListesiGetir());
            secilen_isim.setText("Kimse seçilmedi.");
            txtf_ad_soyad.clear();  // ekle textfield içini temizle
            chbx_cinsiyet.setValue(null); // choicebox sıfırla
            JOptionPane.showMessageDialog(null, "Tüm Kayıtlar Başarıyla Silindi","BİLGİ", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // seçilen pane'i görünür kılar diğerini gizler.
    public void makeVisible(String pane_name){
        if (pane_name.equals("secme ekrani")){
            pn_sec.setVisible(true);
            pn_sinif_liste.setVisible(false);
        }else {
            pn_sec.setVisible(false);
            pn_sinif_liste.setVisible(true);
        }       
    }
    
    // sınıf liste görüntüleme pane aktif hale getirir.
    public void set_sinif_liste(ActionEvent event){
        makeVisible("sinif liste ekrani");
    }
    
    //veritabanında hiç öğrenci yoksa uyarı göster sınıf liste ekranına atlar
    public void bos_OgrenciListe_uyari(){
        JOptionPane.showMessageDialog(null, 
                "Hiç Öğrenci Yok.\nLütfen Önce Öğrenci Ekleyin","UYARI", 
                JOptionPane.WARNING_MESSAGE);
        makeVisible("sinif liste ekrani");// sınıf listesi pane geçer.
    }
    
    //butonlara tıklama efekti ekleyem metod
    public void btnAnimation(Button btn){
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), btn);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
        scaleTransition.setOnFinished(e -> {
            ScaleTransition reverseTransition = new ScaleTransition(Duration.millis(100), btn);
            reverseTransition.setToX(1);
            reverseTransition.setToY(1);
            reverseTransition.play();
        });
    }
    
}
