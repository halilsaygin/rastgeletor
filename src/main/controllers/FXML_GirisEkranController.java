package main.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import main.PushAnimation;
import main.SceneController;

public class FXML_GirisEkranController implements Initializable {
     SceneController sceneController;
     
    @FXML
    public Button btn_gruplayici, btn_rastgeletor;
    
    @FXML
    // Gruplama Ekran Açar
    public void gruplayiciEkranAc(){
        System.out.println("gruplayıcı tıklandı");
         PushAnimation.nodeAnimation(btn_gruplayici);
        sahneGecisiYap("FXML_Gruplandirma.fxml");
    }
    
    // Rastgeletör ekranını açar
    public void rastgeletorEkranAc(){
         System.out.println("rastgeletor tıklandı");
        PushAnimation.nodeAnimation(btn_rastgeletor);
        sahneGecisiYap("FXML_OgrenciSec.fxml");
    }
    
    // Öğrenci listesi ekranını açar.
    public void ogrenciListEkranAc(){
        System.out.println("öğrenci listesi ekranı tıklandı");
        sahneGecisiYap("FXML_OgrenciList.fxml");
    }
    
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
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.err.println("Sahne geçişinde hata oldu.\n" + e.getMessage());
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        sceneController = SceneController.getInstance();
    }   
    
    
}
