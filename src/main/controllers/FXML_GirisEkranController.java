package main.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import main.AlertDialog;
import main.ConnectionFactory;
import main.Ogrenci;
import main.PushAnimation;
import main.SceneController;
import main.daos.OgrenciDAO;
import main.services.OgrenciServis;

public class FXML_GirisEkranController implements Initializable {
     SceneController sceneController;
     OgrenciServis ogrenciServis;
      ObservableList<Ogrenci> ogrenciler;
     
    @FXML
    public Button btn_gruplayici, btn_rastgeletor;
    
    @FXML
    // Gruplama Ekran Açar
    public void gruplayiciEkranAc(){
         PushAnimation.nodeAnimation(btn_gruplayici);
          if (ogrenciler.isEmpty()){
                   int  result = AlertDialog.gosterConfirm("Hiç öğrenci kayıtlı değil. Eklemek ister misiniz?");
                   if (result == 1){
                       sahneGecisiYap("FXML_OgrenciList.fxml");
                   }
          } else {
                sahneGecisiYap("FXML_Gruplandirma.fxml");
          } 
        
    }
    
    // Rastgeletör ekranını açar
    public void rastgeletorEkranAc(){
        PushAnimation.nodeAnimation(btn_rastgeletor);
          if (ogrenciler.isEmpty()){
                   int  result = AlertDialog.gosterConfirm("Hiç öğrenci kayıtlı değil. Eklemek ister misiniz?");
                   if (result == 1){
                       sahneGecisiYap("FXML_OgrenciList.fxml");
                   }
          } else {
                sahneGecisiYap("FXML_OgrenciSec.fxml");
          } 
    }
    
    // Öğrenci listesi ekranını açar.
    public void ogrenciListEkranAc(){
        sahneGecisiYap("FXML_OgrenciList.fxml");
    }
    
    // tüm kayıtları siler.
    public void tumKayitlariSil(){
         try {
            int  result = AlertDialog.gosterConfirm("Tüm öğrenciler silinecek onaylıyor musunuz?");
            if (result == 1){
                ogrenciServis.tumOgrenciKayitlariSil();
                AlertDialog.gosterAlert("Başarılı", "Tüm öğrenciler silindi.", Alert.AlertType.INFORMATION);
            } 
         } catch (SQLException ex) {
                AlertDialog.gosterAlert("Hata", "Bir hata oluştu: " + ex.getMessage(), Alert.AlertType.ERROR);
         }
    }
    
    // sahneler arasında geçiş yapmayı ayarlar.
    public void sahneGecisiYap(String sahneAdi) {
        try {
            switch (sahneAdi) {
                case "FXML_Gruplandirma.fxml":
                    sceneController.sahneAc("/main/fxmlfiles/FXML_Gruplandirma.fxml");
                    break;
                case "FXML_OgrenciList.fxml":
                    sceneController.sahneAc("/main/fxmlfiles/FXML_OgrenciList.fxml");
                    break;
                case "FXML_OgrenciSec.fxml":
                    sceneController.sahneAc("/main/fxmlfiles/FXML_OgrenciSec.fxml");
                    break;
            }
        } catch (IOException ex) {
           AlertDialog.gosterAlert("Hata", "Bir hata oluştu: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        sceneController = SceneController.getInstance();
        
        Connection connection = ConnectionFactory.getConnection();
        OgrenciDAO ogrenciDAO = new OgrenciDAO(connection);
        ogrenciServis = new OgrenciServis(ogrenciDAO);
         
         try {
             ogrenciler = ogrenciServis.tumOgrencileriGetir();
         } catch (SQLException ex) {
             AlertDialog.gosterAlert("Hata", "Bir hata oluştu: " + ex.getMessage(), Alert.AlertType.ERROR);
         }
    }   
    
}
