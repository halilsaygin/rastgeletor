/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package main.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import main.SceneController;


public class FXML_GruplandirmaController implements Initializable {

    @FXML
    public Button btn_onceki_grup, btn_sonraki_grup;
    public ImageView imageview_home;
    
    @FXML
    public void oncekiGetir(){
        
    }
    
    public void sonrakiGetir(){
        
    }
    
    public void anaSahneAc() throws IOException{
      SceneController controller = SceneController.getInstance();
        controller.anaSahne_Ac();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
