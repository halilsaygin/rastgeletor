/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package main;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FXMain extends Application {
     public static Stage _primaryStage;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        _primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("fxmlfiles/FXML_GirisEkran.fxml"));
        String css = this.getClass().getResource("ui_styles.css").toExternalForm();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(css) ;
        _primaryStage.setScene(scene);
        Image icon = new Image(getClass().getResourceAsStream("uyg_icon.png"));
        _primaryStage.getIcons().add(icon);
        _primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
