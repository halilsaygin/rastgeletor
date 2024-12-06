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
import main.AlertDialog;
import main.ConnectionFactory;
import main.Ogrenci;
import main.PushAnimation;
import main.SceneController;
import main.daos.OgrenciDAO;
import main.services.OgrenciServis;

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
            ogrenciListGuncelle(); // liste ve tableview güncelleme
            txtf_ad_soyad.clear();
            AlertDialog.gosterAlert("Başarılı", "Öğrenci başarıyla eklendi.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            AlertDialog.gosterAlert("Hata", "Öğrenci eklenirken bir hata oluştu: " + e.getMessage(), Alert.AlertType.ERROR);
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
                    AlertDialog.gosterAlert("Hata", "Öğrenci silinirken bir hata oluştu: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } else {
            AlertDialog.gosterAlert("Uyarı", "Silmek için bir öğrenci seçin!", Alert.AlertType.WARNING);
        }
    }

    // ana sahneye dönme.
    public void anaSahneAc() throws IOException {
        SceneController controller = SceneController.getInstance();
        controller.anaSahne_Ac();
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
            ogrenciList.clear(); // Mevcut listeyi temizle
            ogrenciList.addAll(ogrenciServis.tumOgrencileriGetir()); // Yeni verileri ekle
        } catch (SQLException ex) {
            AlertDialog.gosterAlert("Hata", "Bir hata oluştu: " + ex.getMessage(), Alert.AlertType.ERROR);
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
        Connection connection = ConnectionFactory.getConnection();
        OgrenciDAO ogrenciDAO = new OgrenciDAO(connection);
        ogrenciServis = new OgrenciServis(ogrenciDAO);
        silButonuGosterGizle();

        // column-ları tanımla.
        adSoyadSutunu.setCellValueFactory(new PropertyValueFactory<>("adSoyad"));
        cinsiyetSutunu.setCellValueFactory(new PropertyValueFactory<>("cinsiyet"));

        secilenCinsiyet = "Erkek";
        cinsiyetSecimiAl(); // seçillmiş cinsiyeti alır. değişiminde atama yapar. 

        // ObservableList'i başlat ve verileri yükle
        ogrenciList = FXCollections.observableArrayList();
        ogrenciListGuncelle();
        tblview_ogrenci_liste.setItems(ogrenciList);
    }

}
