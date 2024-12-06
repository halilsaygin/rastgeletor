package main.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import main.AlertDialog;
import main.ConnectionFactory;
import main.DialogGroupRandomOptions;
import main.FXMain;
import main.Ogrenci;
import main.PushAnimation;
import main.SceneController;
import main.daos.OgrenciDAO;
import main.services.OgrenciServis;

public class FXML_GruplandirmaController implements Initializable {

    OgrenciServis ogrenciServis;
    ObservableList<ObservableList<Ogrenci>> olusturulanGruplar;
    int gosterilenGrupIndex;

    @FXML
    public Button btn_onceki_grup, btn_sonraki_grup;
    public Label label_grupNo;
    public ListView listvw_grup;

    @FXML
    // önceki grubu listview içine getirir.
    public void oncekiGetir() {
        PushAnimation.nodeAnimation(btn_onceki_grup);
        if (gosterilenGrupIndex - 1 >= 0) {
            gosterilenGrupIndex--;
            listViewGoster(olusturulanGruplar.get(gosterilenGrupIndex));
            grupSiraNoGoster(gosterilenGrupIndex);
        } else {
            AlertDialog.gosterAlert("BİLGİ", "Bu ilk olandı :) ", Alert.AlertType.INFORMATION);
        }
    }

    // sonraki grubu listview içine getirir.
    public void sonrakiGetir() {
        PushAnimation.nodeAnimation(btn_sonraki_grup);
        if (gosterilenGrupIndex + 1 < olusturulanGruplar.size()) {
            gosterilenGrupIndex++;
            listViewGoster(olusturulanGruplar.get(gosterilenGrupIndex));
            grupSiraNoGoster(gosterilenGrupIndex);
        } else {
            AlertDialog.gosterAlert("BİLGİ", "Bu Sonuncuydu :) ", Alert.AlertType.INFORMATION);
        }
    }

    // yeni gruplama seçenek diyalog ekranı açar.
    public void yeniGruplamaBaslat() {
        olusturulanGruplar.clear();
        gosterilenGrupIndex = 0;

        DialogGroupRandomOptions dialog = new DialogGroupRandomOptions(FXMain._primaryStage);
        dialog.showCustomDialog();

        int grupKisiSayisi = dialog.getGrupKisiSayisi();
        int grupSayisi = dialog.getGrupSayisi();

        // kullanıcı dialog penceresini iptal ederse...
        try {
            if (grupKisiSayisi == 0 && grupSayisi != 0) {  // grup sayısı seçilmiş.
                olusturulanGruplar = ogrenciServis.rastgeleGruplarOlustur(true, grupSayisi);
                listViewGoster(olusturulanGruplar.get(gosterilenGrupIndex));
                grupSiraNoGoster(0);
            } else if (grupKisiSayisi != 0 && grupSayisi == 0) { // gruptaki kişi sayısı seçilmiş...
                olusturulanGruplar = ogrenciServis.rastgeleGruplarOlustur(false, grupKisiSayisi);
                listViewGoster(olusturulanGruplar.get(gosterilenGrupIndex));
                grupSiraNoGoster(0);
            }
        } catch (SQLException ex) {
            AlertDialog.gosterAlert("Hata", "Bir hata oluştu: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ana sahneye dön
    public void anaSahneAc() throws IOException {
        SceneController controller = SceneController.getInstance();
        controller.anaSahne_Ac();
    }

    // isimleri listview içinde gösterir
    private void listViewGoster(ObservableList<Ogrenci> grup) {
        ObservableList<String> grupKisiAdSoyad = FXCollections.observableArrayList();
        for (Ogrenci ogrenci : grup) {
            grupKisiAdSoyad.add(ogrenci.getAdSoyad());
        }
        System.out.println(grupKisiAdSoyad.toString());
        listvw_grup.setItems(grupKisiAdSoyad);
        listvw_grup.refresh();
    }

    // kaçıncı grup olduğunu gösteren labeli günceller. 
    private void grupSiraNoGoster(int grupIndex) {
        label_grupNo.setText(String.valueOf(grupIndex + 1) + ".Grup");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Connection connection = ConnectionFactory.getConnection();
        OgrenciDAO ogrenciDAO = new OgrenciDAO(connection);
        ogrenciServis = new OgrenciServis(ogrenciDAO);
        gosterilenGrupIndex = 0;
        olusturulanGruplar = FXCollections.observableArrayList();
    }

}
