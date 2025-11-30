package main.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import main.utils.AlertDialog;
import main.utils.ErrorLogger;
import main.database.ConnectionFactory;
import main.utils.PushAnimation;
import main.dialogboxes.DialogSelectRandomOptions;
import main.FXMain;
import main.database.Ogrenci;
import main.utils.SceneController;
import main.database.OgrenciDAO;
import main.database.OgrenciServis;

public class FXML_OgrenciSecController implements Initializable {

    OgrenciServis ogrenciServis;
     Random random;
    public String liste_modu; // azalan_liste veya sabit_liste
    public String cinsiyet_modu; // tamami , kiz, erkek
    public ObservableList<Ogrenci> ogrenciListesi;
    int onceki_indeks;

    @FXML
    public Button btn_sec;
    public Label secilen_isim;

    @FXML
    public void anaSahneAc() {
        try {
            SceneController controller = SceneController.getInstance();
            controller.anaSahne_Ac();
        } catch (IOException ex) {
            ErrorLogger.log("OgrenciSec - anaSahneAc", ex);
            AlertDialog.gosterAlert("Hata", "Ana ekrana dönülürken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }

    // Her tıklamada yeni öğrenci seçer.
    public void yeniOgrenciSec() {
        PushAnimation.nodeAnimation(btn_sec);

        Ogrenci rastgeleOgrenci = null;
        if (!ogrenciListesi.isEmpty()) {
            if (liste_modu.equals("eksilen_liste")) {
                rastgeleOgrenci = ogrenciListesi.remove(0);
            } else {
                int randomIndeks;
                do{
                    randomIndeks = random.nextInt(ogrenciListesi.size());
                }while(randomIndeks == onceki_indeks);
 
                rastgeleOgrenci = ogrenciListesi.get(randomIndeks);
                onceki_indeks = randomIndeks;
            }

            secilen_isim.setText(rastgeleOgrenci.getAdSoyad());
        } else {
            secilen_isim.setText("LİSTE BOŞ!");
        }

    }

    // menuItem click event. yeni tur ayarlama dialog penceresi açar.
    public void yeniTurAyarla() {
        DialogSelectRandomOptions dialog = new DialogSelectRandomOptions(FXMain._primaryStage);
        dialog.showCustomDialog();

        liste_modu = dialog.getSecilenListeModu();
        cinsiyet_modu = dialog.getSecilenCinsiyetModu();

        ogrenciListesi.clear();
        try {
            ogrenciListesi = ogrenciServis.karisikOgrenciListesiGetir(cinsiyet_modu);
        } catch (SQLException ex) {
            ErrorLogger.log("OgrenciSec - yeniTurAyarla", ex);
            AlertDialog.gosterAlert("Hata", "Liste yüklenirken bir hata oluştu.", Alert.AlertType.ERROR);
        }
        secilen_isim.setText("Seçmek için tıkla!");
        System.out.println("Seçilen Liste Modu: " + liste_modu);
        System.out.println("Seçilen Cinsiyet Modu: " + cinsiyet_modu);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection connection = ConnectionFactory.getConnection();
            OgrenciDAO ogrenciDAO = new OgrenciDAO(connection);
            ogrenciServis = new OgrenciServis(ogrenciDAO);
             random = new Random();
             onceki_indeks = -1;
            ogrenciListesi = FXCollections.observableArrayList();

            liste_modu = "eksilen_liste";
            cinsiyet_modu = "tamami";

            ogrenciListesi = ogrenciServis.karisikOgrenciListesiGetir(cinsiyet_modu);
        } catch (SQLException ex) {
            ErrorLogger.log("OgrenciSec - initialize", ex);
            AlertDialog.gosterAlert("Hata", "Ekran yüklenirken bir hata oluştu.", Alert.AlertType.ERROR);
        }

    }

}
