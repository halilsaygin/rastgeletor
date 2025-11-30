package main.utils;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.FXMain;

public class AlertDialog {
    
    public static void gosterAlert(String baslik, String mesaj, Alert.AlertType uyarıTipi) {
        Alert alert = new Alert(uyarıTipi);
        alert.setTitle(baslik);
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        
        // Ana pencereyi owner olarak ayarla
        alert.initOwner(FXMain._primaryStage);
        
        // Uygulama ikonunu ekle
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(AlertDialog.class.getResourceAsStream("/icons/uyg_icon.png")));

        // Ana pencerenin ortasında konumlandır
        alert.showAndWait();
    }

    public static int gosterConfirm(String mesaj) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("ONAY");
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        
        // Türkçe buton isimleri
        ButtonType evetButton = new ButtonType("Evet");
        ButtonType hayirButton = new ButtonType("Hayır");
        alert.getButtonTypes().setAll(evetButton, hayirButton);
        
        // Ana pencereyi owner olarak ayarla
        alert.initOwner(FXMain._primaryStage);
        
        // Uygulama ikonunu ekle
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(AlertDialog.class.getResourceAsStream("/icons/uyg_icon.png")));

        // Ana pencerenin ortasında konumlandır
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == evetButton) {
            return 1;
        } else {
            return 0;
        }
    }

}
