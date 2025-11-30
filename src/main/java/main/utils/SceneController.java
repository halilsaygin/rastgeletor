package main.utils;

import java.io.IOException;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.FXMain;

public class SceneController {

    private static SceneController instance;
    private final Stage stage = FXMain._primaryStage;
    private final double DURATION = 0.8;
    private final String ANA_SAHNE_FXML = "/fxmlfiles/FXML_GirisEkran.fxml";
    private final String ANA_SAHNE_STIL_SAYFA = "/styles/giris_ekran.css";

    public static SceneController getInstance() {
        if (instance == null) {
            instance = new SceneController();
        }
        return instance;
    }

    public void sahneAc(String fxml_name) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml_name));
        Scene scene = new Scene(root);

        cssAyarla(scene);

        stage.setScene(scene);
        sahneGecisAnimasyon(root,scene,false);
    }

    public void anaSahne_Ac() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(ANA_SAHNE_FXML));
        Scene scene = new Scene(root);
        
        cssAyarla(scene);

        // sahnenin görünmemesini sağlayan bir geçiş süresi ekle
        stage.setScene(scene);
        sahneGecisAnimasyon(root,scene,true);
        
    }

    public void sahneGecisAnimasyon(Parent root,Scene scene, boolean isReverse) {
        // Sahne genişliğini alarak dinamik bir şekilde animasyonu başlat
        double sceneWidth = scene.getWidth();
        TranslateTransition transition = new TranslateTransition(Duration.seconds(DURATION), root);
        if (isReverse) transition.setFromX(-sceneWidth); // Sola doğru başlat
        else transition.setFromX(sceneWidth);
        transition.setToX(0); // Hedef konum
        transition.play();
    }
    
    public void cssAyarla(Scene scene){
         // ayarla css dosyası
        String css = this.getClass().getResource(ANA_SAHNE_STIL_SAYFA).toExternalForm();
        scene.getStylesheets().add(css);
    }

}
