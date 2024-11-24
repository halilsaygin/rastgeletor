package main.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import main.ConnectionFactory;
import main.DialogGroupRandomOptions;
import main.FXMain;
import main.PushAnimation;
import main.SceneController;
import main.daos.OgrenciDAO;
import main.services.OgrenciServis;


public class FXML_GruplandirmaController implements Initializable {
    OgrenciServis ogrenciServis;
    
    @FXML
    public Button btn_onceki_grup, btn_sonraki_grup;
    public Label label_grupNo;
    public ListView listvw_grup;
    
    @FXML
    // önceki grubu listview içine getirir.
    public void oncekiGetir(){
        PushAnimation.nodeAnimation(btn_onceki_grup);
    }
    
    // sonraki grubu listview içine getirir.
    public void sonrakiGetir(){
         PushAnimation.nodeAnimation(btn_sonraki_grup);
    }
    
    // yeni gruplama seçenek diyalog ekranı açar.
    public void yeniGruplamaBaslat(){
          DialogGroupRandomOptions dialog = new DialogGroupRandomOptions(FXMain._primaryStage);
            dialog.showCustomDialog();
            
            int grupKisiSayisi = dialog.getGrupKisiSayisi();
            
            System.out.println("Grup kişi sayısı: " + grupKisiSayisi);
            
    }
    
    public void anaSahneAc() throws IOException{
      SceneController controller = SceneController.getInstance();
        controller.anaSahne_Ac();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Connection connection = ConnectionFactory.getConnection();
        OgrenciDAO ogrenciDAO = new OgrenciDAO(connection);
        ogrenciServis = new OgrenciServis(ogrenciDAO);
    }    
    
}
