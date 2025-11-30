package main;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.utils.ErrorLogger;

public class FXMain extends Application {
     public static Stage _primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            _primaryStage = primaryStage;
            
            // Modül sisteminde resource yükleme
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxmlfiles/FXML_GirisEkran.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            
            _primaryStage.setScene(scene);
            _primaryStage.setResizable(false);
            
            Image icon = new Image(getClass().getResourceAsStream("/icons/uyg_icon.png"));
            if (icon != null) {
                _primaryStage.getIcons().add(icon);
            }
            
            _primaryStage.show();
            
            ErrorLogger.log("Uygulama Başlatıldı", "Başarıyla açıldı");
        } catch (Exception e) {
            ErrorLogger.log("FXMain - start DETAY", 
                "Hata: " + e.getClass().getName() + "\n" +
                "Mesaj: " + e.getMessage() + "\n" +
                "Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "yok"));
            System.err.println("Uygulama başlatılamadı: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ErrorLogger.log("Uygulama Başlangıç", "main() çağrıldı");
            launch(args);
        } catch (Exception e) {
            ErrorLogger.log("FXMain - main", e);
            System.err.println("Kritik hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
