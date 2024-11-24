package main;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogGroupRandomOptions {
     Stage parentStage;
    int grupKisiSayisi;

    public DialogGroupRandomOptions(Stage parentStage) {
        this.parentStage = parentStage;
        this.grupKisiSayisi = 2; // Varsayılan grup büyüklüğü
    }

    public void showCustomDialog() {
        // Ana diyalog penceresi
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL); // Modality: Parent engellenir
        dialogStage.setResizable(false);
        dialogStage.initOwner(this.parentStage);
        dialogStage.setTitle("Grup Seçeneklerini Ayarla");

        // Ana pencere ikonunu kopyala
        if (this.parentStage.getIcons() != null && !this.parentStage.getIcons().isEmpty()) {
            dialogStage.getIcons().addAll(this.parentStage.getIcons());
        }

        // Bilgilendirici bir Label
        Label infoLabel = new Label("Kaç kişilik gruplar oluşturulsun?");
        infoLabel.setStyle("-fx-font-size: 18px;");

        // Grup büyüklüğü için bir TextField
        TextField groupSizeField = new TextField(String.valueOf(this.grupKisiSayisi)); // Varsayılan değer
        groupSizeField.setPromptText("Örn: 3");
        groupSizeField.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
        groupSizeField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Kullanıcı yalnızca sayısal değerler girebilir
            if (!newValue.matches("\\d*")) {
                groupSizeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Tamam Butonu
        Button confirmButton = new Button("Tamam");
        confirmButton.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 10px;");
        confirmButton.setOnAction(event -> {
            // Grup büyüklüğünü al
            String input = groupSizeField.getText();
            if (!input.isEmpty()) {
                try {
                    this.grupKisiSayisi = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    // Geçerli bir sayı değilse varsayılan değeri kullan
                    this.grupKisiSayisi = 2;
                }
            }
            dialogStage.close(); // Diyalog kapat
        });

        // Buton için HBox: Yatayda ortalamak için
        HBox buttonBox = new HBox(confirmButton);
        buttonBox.setStyle("-fx-alignment: center;");

        // Layout ayarları
        VBox dialogLayout = new VBox(15, infoLabel, groupSizeField, buttonBox);
        dialogLayout.setStyle("-fx-padding: 15; -fx-alignment: center;");

        Scene dialogScene = new Scene(dialogLayout, 300, 200);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait(); // Diyalog bloklanır
    }

    public int getGrupKisiSayisi() {
        return grupKisiSayisi;
    }
}
