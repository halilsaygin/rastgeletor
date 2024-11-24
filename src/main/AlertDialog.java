package main;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertDialog {
    
    public static void gosterAlert(String baslik, String mesaj, Alert.AlertType uyarıTipi) {
        Alert alert = new Alert(uyarıTipi);
        alert.setTitle(baslik);
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);

        alert.showAndWait();
    }

    public static int gosterConfirm(String mesaj) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("ONAY");
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.YES) {
            return 1;
        } else {
            return 0;
        }
    }

}
