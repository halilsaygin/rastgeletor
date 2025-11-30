package main.dialogboxes;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogSelectRandomOptions  {
    Stage parentStage;
    String secilenListeModu;
    String secilenCinsiyetModu;
    
    public DialogSelectRandomOptions(Stage parentStage){
        this.parentStage = parentStage;
        this.secilenListeModu = "azalan_liste";
        this.secilenCinsiyetModu = "tamami";
    }
   
    public void showCustomDialog() {
    // Ana diyalog penceresi
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL); // Modality: Parent engellenir
        dialogStage.setResizable(false);
        //dialogStage.sizeToScene();
        dialogStage.initOwner(this.parentStage);
        dialogStage.setTitle("Yeni Seçim Listesi  Ayarla");
        
        // Ana pencere ikonunu kopyala
        if (this.parentStage.getIcons() != null && !this.parentStage.getIcons().isEmpty()) {
            dialogStage.getIcons().addAll(this.parentStage.getIcons());
        }

        // 1. RadioButton grubu: Liste tipi
        ToggleGroup listTypeGroup = new ToggleGroup();

        RadioButton decreasingList = new RadioButton("Eksilen Liste");
        decreasingList.setStyle("-fx-font-size: 18px;"); // Yazı boyutu büyütüldü
        decreasingList.setToggleGroup(listTypeGroup);
        decreasingList.setSelected(true); // Varsayılan seçili

        RadioButton fixedList = new RadioButton("Sabit Liste");
        fixedList.setStyle("-fx-font-size: 18px;");
        fixedList.setToggleGroup(listTypeGroup);

        VBox listTypeBox = new VBox(10, decreasingList, fixedList);
        listTypeBox.setSpacing(10);
        listTypeBox.setStyle("-fx-border-color: lightgray; -fx-padding: 10; -fx-border-radius: 5;");

        // 2. RadioButton grubu: Cinsiyet tipi
        ToggleGroup genderGroup = new ToggleGroup();

        RadioButton mixed = new RadioButton("Tüm Sınıf");
        mixed.setToggleGroup(genderGroup);
        mixed.setStyle("-fx-font-size: 18px;");
        mixed.setSelected(true); // Varsayılan seçili

        RadioButton onlyGirls = new RadioButton("Sadece Kızlar");
        onlyGirls.setStyle("-fx-font-size: 18px;");
        onlyGirls.setToggleGroup(genderGroup);

        RadioButton onlyBoys = new RadioButton("Sadece Erkekler");
        onlyBoys.setStyle("-fx-font-size: 18px;");
        onlyBoys.setToggleGroup(genderGroup);

        VBox genderTypeBox = new VBox(10, mixed, onlyGirls, onlyBoys);
        genderTypeBox.setSpacing(10);
        genderTypeBox.setStyle("-fx-border-color: lightgray; -fx-padding: 10; -fx-border-radius: 5;");

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
            // Seçimleri logla
            String secilenListeModu = ((RadioButton) listTypeGroup.getSelectedToggle()).getText();
            ayarlaListeModu(secilenListeModu);
            
            String secilenCinsiyetModu= ((RadioButton) genderGroup.getSelectedToggle()).getText();
            ayarlaCinsiyetModu(secilenCinsiyetModu);
           
            dialogStage.close(); // Diyalog kapat
        });
        
        // Buton için HBox: Yatayda ortalamak için
        HBox buttonBox = new HBox(confirmButton);
        buttonBox.setStyle("-fx-alignment: center;");

        // Layout ayarları
        VBox dialogLayout = new VBox(15, listTypeBox, genderTypeBox, buttonBox);
        dialogLayout.setStyle("-fx-padding: 15;");

        Scene dialogScene = new Scene(dialogLayout, 300, 320);
        dialogStage.setScene(dialogScene);
        
        // Ana pencerenin ortasında konumlandır
        dialogStage.setOnShown(e -> {
            dialogStage.setX(parentStage.getX() + (parentStage.getWidth() - dialogStage.getWidth()) / 2);
            dialogStage.setY(parentStage.getY() + (parentStage.getHeight() - dialogStage.getHeight()) / 2);
        });
        
        dialogStage.showAndWait();
    }
    
     private void  ayarlaListeModu(String listeMod){
        if (listeMod.equals("Eksilen Liste")) this.secilenListeModu="eksilen_liste";
        else this.secilenListeModu="sabit_liste";
    }
    
    private void ayarlaCinsiyetModu(String cinsiyetMod){
        if (cinsiyetMod.equals("Tüm Sınıf")) this.secilenCinsiyetModu="tamami";
        else if (cinsiyetMod.equals("Sadece Erkekler")) this.secilenCinsiyetModu="erkek";
        else this.secilenCinsiyetModu="kiz";
    }
    
    public String getSecilenListeModu() {
        return secilenListeModu;
    }

    public String getSecilenCinsiyetModu() {
        return secilenCinsiyetModu;
    }
    
}
