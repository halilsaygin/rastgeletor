package main.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import main.utils.AlertDialog;
import main.utils.ErrorLogger;
import main.database.ConnectionFactory;
import main.database.Ogrenci;
import main.utils.PushAnimation;
import main.utils.SceneController;
import main.database.OgrenciDAO;
import main.database.OgrenciServis;

public class FXML_OgrenciListController implements Initializable {

    OgrenciServis ogrenciServis;
    String secilenCinsiyet;
    ObservableList<Ogrenci> ogrenciList;

    @FXML
    public Button btn_ogrenci_ekle, btn_sil;
    public Label lbl_durum;
    public TextField txtf_ad_soyad;
    public RadioButton radio_cinsiyetErkek, cinsiyetKiz;
    public ToggleGroup cinsiyet;
    public TableView tblview_ogrenci_liste;
    public TableColumn<Ogrenci, String> adSoyadSutunu, cinsiyetSutunu;

    // öğrenci ekleme buton tıklama meteodu
    public void ekle_ogrenci() {
        PushAnimation.nodeAnimation(btn_ogrenci_ekle);
        String adSoyad = txtf_ad_soyad.getText().trim();
        if (adSoyad.isEmpty()) {
            AlertDialog.gosterAlert("Uyarı", "Girdi Alanı Boş!", Alert.AlertType.WARNING);
            return;
        }
        try {
            ogrenciServis.ogrenciEkle(adSoyad, secilenCinsiyet);
            ogrenciListGuncelle();
            txtf_ad_soyad.clear();
            AlertDialog.gosterAlert("Başarılı", "Öğrenci başarıyla eklendi.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            ErrorLogger.log("OgrenciList - ekle_ogrenci", e);
            AlertDialog.gosterAlert("Hata", "Öğrenci eklenirken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }

    // seçilmiş bir öğrenciyi silme buton tıklama metodu
    public void sil_ogrenci() {
        PushAnimation.nodeAnimation(btn_sil);
        Ogrenci secilenOgrenci = (Ogrenci) tblview_ogrenci_liste.getSelectionModel().getSelectedItem();
        if (secilenOgrenci != null) {
            System.out.println("Silinecek Öğrenci: " + secilenOgrenci.getAdSoyad());
            // Silme 
            int cevap = AlertDialog.gosterConfirm("Öğrenci silinecek. Onaylıyor musunuz?");
            if (cevap == 1) {
                int ogrenciId = secilenOgrenci.getId();  // Ogrenci sınıfında ID'yi almak için getter yazmalısınız.
                try {
                    ogrenciServis.ogrenciSil(ogrenciId);
                    ogrenciListGuncelle();
                    AlertDialog.gosterAlert("Başarılı", "Öğrenci başarıyla silindi.", Alert.AlertType.INFORMATION);
                } catch (SQLException ex) {
                    ErrorLogger.log("OgrenciList - sil_ogrenci", ex);
                    AlertDialog.gosterAlert("Hata", "Öğrenci silinirken bir hata oluştu.", Alert.AlertType.ERROR);
                }
            }
        } else {
            AlertDialog.gosterAlert("Uyarı", "Silmek için bir öğrenci seçin!", Alert.AlertType.WARNING);
        }
    }

    // ana sahneye dönme.
    public void anaSahneAc() {
        try {
            SceneController controller = SceneController.getInstance();
            controller.anaSahne_Ac();
        } catch (IOException ex) {
            ErrorLogger.log("OgrenciList - anaSahneAc", ex);
            AlertDialog.gosterAlert("Hata", "Ana ekrana dönülürken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }

    // cinsiyet değişince algılarve atama yapar.
    private void cinsiyetSecimiAl() {
        cinsiyet.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) -> {
            RadioButton rb = (RadioButton) cinsiyet.getSelectedToggle();

            if (rb != null) {
                secilenCinsiyet = rb.getText();
                System.out.println("Cinsiyet seçildi: " + secilenCinsiyet);
            }
        });
    }

    // öğrenci liste güncelleme yapar.
    private void ogrenciListGuncelle() {
        try {
            ogrenciList.clear();
            ogrenciList.addAll(ogrenciServis.tumOgrencileriGetir());
        } catch (SQLException ex) {
            ErrorLogger.log("OgrenciList - ogrenciListGuncelle", ex);
            AlertDialog.gosterAlert("Hata", "Liste güncellenirken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }

    // sil butonu satır seçilme durumuna göre gösterir ya da gizler.
    private void silButonuGosterGizle() {
        tblview_ogrenci_liste.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                btn_sil.setVisible(true);
            } else {
                btn_sil.setVisible(false);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection connection = ConnectionFactory.getConnection();
            OgrenciDAO ogrenciDAO = new OgrenciDAO(connection);
            ogrenciServis = new OgrenciServis(ogrenciDAO);
            silButonuGosterGizle();

            adSoyadSutunu.setCellValueFactory(new PropertyValueFactory<>("adSoyad"));
            cinsiyetSutunu.setCellValueFactory(new PropertyValueFactory<>("cinsiyet"));

            secilenCinsiyet = "Erkek";
            cinsiyetSecimiAl();

            ogrenciList = FXCollections.observableArrayList();
            ogrenciListGuncelle();
            tblview_ogrenci_liste.setItems(ogrenciList);
        } catch (Exception ex) {
            ErrorLogger.log("OgrenciList - initialize", ex);
            AlertDialog.gosterAlert("Hata", "Ekran yüklenirken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }

}
