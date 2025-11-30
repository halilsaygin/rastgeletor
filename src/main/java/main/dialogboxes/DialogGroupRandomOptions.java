package main.dialogboxes;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogGroupRandomOptions {
    Stage parentStage;
    int grupKisiSayisi;
    int grupSayisi;

    public DialogGroupRandomOptions(Stage parentStage) {
        this.parentStage = parentStage;
        this.grupKisiSayisi = 2; // Varsayılan grup büyüklüğü
        this.grupSayisi = 2; // Varsayılan grup sayısı
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
        Label infoLabel = new Label("Grup Oluşturma Seçenekleri");
        infoLabel.setStyle("-fx-font-size: 18px;");

        // RadioButtons ve ToggleGroup
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton byGroupSizeRadio = new RadioButton("Gruptaki kişi sayısı ile");
        RadioButton byGroupCountRadio = new RadioButton("Grup sayısı ile");
        byGroupSizeRadio.setStyle("-fx-font-size: 14px;");
         byGroupCountRadio.setStyle("-fx-font-size: 14px;");
        byGroupSizeRadio.setToggleGroup(toggleGroup);
        byGroupCountRadio.setToggleGroup(toggleGroup);
        byGroupCountRadio.setSelected(true); // Varsayılan seçim

        // Grup büyüklüğü için bir TextField
        TextField groupSizeField = new TextField(String.valueOf(this.grupKisiSayisi)); // Varsayılan değer
        groupSizeField.setPromptText("Örn: 3");
        groupSizeField.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
        groupSizeField.setDisable(true);
        groupSizeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                groupSizeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Grup sayısı için bir TextField
        TextField groupCountField = new TextField(String.valueOf(this.grupSayisi)); // Varsayılan değer
        groupCountField.setPromptText("Örn: 5");
        groupCountField.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
        groupCountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                groupCountField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // RadioButton'ların TextField'leri etkinleştirme/deaktif etme işlevi
        byGroupSizeRadio.setOnAction(event -> {
            groupSizeField.setDisable(false);
            groupCountField.setDisable(true);
        });

        byGroupCountRadio.setOnAction(event -> {
            groupSizeField.setDisable(true);
            groupCountField.setDisable(false);
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
            if (byGroupSizeRadio.isSelected()) { // Gruptaki kişi sayısı seçilmişse
                String input = groupSizeField.getText();
                if (!input.isEmpty()) {
                    try {
                        this.grupSayisi = 0;
                        this.grupKisiSayisi = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        this.grupKisiSayisi = 2;
                    }
                }
            } else if (byGroupCountRadio.isSelected()) { // Grup sayısı seçilmişse
                String input = groupCountField.getText();
                if (!input.isEmpty()) {
                    try {
                        this.grupKisiSayisi = 0;
                        this.grupSayisi = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        this.grupSayisi = 2;
                    }
                }
            }
            dialogStage.close(); // Diyalog kapat
        });

        // Layout ayarları
        VBox radioButtonBox = new VBox(10, byGroupCountRadio, groupCountField, byGroupSizeRadio, groupSizeField );
        radioButtonBox.setStyle("-fx-alignment: center-left;");
        VBox dialogLayout = new VBox(15, infoLabel, radioButtonBox, confirmButton);
        dialogLayout.setStyle("-fx-padding: 15; -fx-alignment: center;");

        Scene dialogScene = new Scene(dialogLayout, 400, 300);
        dialogStage.setScene(dialogScene);
        
        // Ana pencerenin ortasında konumlandır
        dialogStage.setOnShown(e -> {
            dialogStage.setX(parentStage.getX() + (parentStage.getWidth() - dialogStage.getWidth()) / 2);
            dialogStage.setY(parentStage.getY() + (parentStage.getHeight() - dialogStage.getHeight()) / 2);
        });
        
        dialogStage.showAndWait();
    }

    public int getGrupKisiSayisi() {
        return this.grupKisiSayisi;
    }

    public int getGrupSayisi() {
        return this.grupSayisi;
    }
}
