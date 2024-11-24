package main.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import main.AlertDialog;
import main.ConnectionFactory;
import main.PushAnimation;
import main.DialogSelectRandomOptions;
import main.FXMain;
import main.Ogrenci;
import main.SceneController;
import main.daos.OgrenciDAO;
import main.services.OgrenciServis;

public class FXML_OgrenciSecController implements Initializable {

    OgrenciServis ogrenciServis;
    @FXML
    public Button btn_sec;
    public Label secilen_isim;

    @FXML
    public void anaSahneAc() throws IOException {
        SceneController controller = SceneController.getInstance();
        controller.anaSahne_Ac();
    }

    public void yeniOgrenciSec() {
        PushAnimation.nodeAnimation(btn_sec);
        Ogrenci rastgeleOgrenci;
        try {
            rastgeleOgrenci = ogrenciServis.rastgeleOgrenciGetir();
            if (rastgeleOgrenci != null) {
                secilen_isim.setText(rastgeleOgrenci.getAdSoyad());
            }
        } catch (SQLException ex) {
            AlertDialog.gosterAlert("Hata", "Bir hata oluştu: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // menuItem click event. yeni tur ayarlama dialog penceresi açar.
    public void yeniTurAyarla() {
        DialogSelectRandomOptions dialog = new DialogSelectRandomOptions(FXMain._primaryStage);
        dialog.showCustomDialog();

        String listeModu = dialog.getSecilenListeModu();
        String cinsiyetModu = dialog.getSecilenCinsiyetModu();

        System.out.println("Seçilen Liste Modu: " + listeModu);
        System.out.println("Seçilen Cinsiyet Modu: " + cinsiyetModu);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Connection connection = ConnectionFactory.getConnection();
        OgrenciDAO ogrenciDAO = new OgrenciDAO(connection);
        ogrenciServis = new OgrenciServis(ogrenciDAO);

    }

}
