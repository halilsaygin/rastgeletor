/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;
import java.sql.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseOperations {

    private Connection connection;
    private final String DB_FILE = "jdbc:sqlite:ogrenciler.db";

    public DatabaseOperations() {
        try {
            // Bağlantı kur
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_FILE);

            // Tablo oluştur
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS OGRENCILER (id INTEGER PRIMARY KEY AUTOINCREMENT, ad_soyad TEXT, cinsiyet TEXT)");
            System.out.println("Veri tabanı bağlantısı başarılı");
        } catch (SQLException | ClassNotFoundException e) {
            e.getMessage();
        }
        
    }

    public void ogrenciEkle(String adSoyad,String cinsiyet) {
        try {
            // Veritabanına kayıt ekle
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO OGRENCILER (ad_soyad, cinsiyet) VALUES (?, ?)");
            preparedStatement.setString(1, adSoyad);
            preparedStatement.setString(2, cinsiyet);
            preparedStatement.executeUpdate();
            System.out.println("Öğrenci ekleme başarılı");
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public int ogrenciIdGetir(String adSoyad) {
        try{
            String sql = "SELECT id FROM OGRENCILER WHERE ad_soyad = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, adSoyad);
            ResultSet resultSet = preparedStatement.executeQuery();
            int id = -1;
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
            return id;
        }  catch (SQLException e) {
            e.getMessage();
            return -1;
        }
        
    }
    
    public void tumKayitlariSil(){
        // Veritabanı tüm kayıtları siler.
        String sql = "DELETE FROM OGRENCILER; DELETE FROM `sqlite_sequence` WHERE `name` = 'OGRENCILER'";
        try {
            // Sorguyu çalıştırma
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            
            // Başarılı mesajı
            System.out.println("Tüm kayıtlar silindi!");
        } catch (SQLException e) {
            // Hata mesajı
            System.out.println("Hata: " + e.getMessage());
        }
    }
    
    public ObservableList<Ogrenci> ogrenciListesiGetir() {
    // ObservableList tipinde bir liste oluştur
        ObservableList<Ogrenci> ogrenciListesi = FXCollections.observableArrayList();
        try {
            // Veritabanından tüm kayıtları seç
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM OGRENCILER");
            // ResultSet'teki her satır için bir Ogrenci nesnesi oluştur ve listeye ekle
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String adSoyad = resultSet.getString("ad_soyad");
                String cinsiyet = resultSet.getString("cinsiyet");
                Ogrenci ogrenci = new Ogrenci(new SimpleIntegerProperty(id), new SimpleStringProperty(adSoyad), new SimpleStringProperty(cinsiyet));
                ogrenciListesi.add(ogrenci);
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        // Listeyi döndür
        return ogrenciListesi;
    }

    public ObservableList<Ogrenci> ogrenciListesiGetir(String cinsiyet) {
    // ObservableList tipinde bir liste oluştur
    ObservableList<Ogrenci> ogrenciListesi = FXCollections.observableArrayList();
    try {
        // Veritabanından cinsiyete göre kayıtları seç
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM OGRENCILER WHERE cinsiyet = ?");
        statement.setString(1, cinsiyet);
        ResultSet resultSet = statement.executeQuery();
        // ResultSet'teki her satır için bir Ogrenci nesnesi oluştur ve listeye ekle
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String adSoyad = resultSet.getString("ad_soyad");
            Ogrenci ogrenci = new Ogrenci(new SimpleIntegerProperty(id), new SimpleStringProperty(adSoyad), new SimpleStringProperty(cinsiyet));
            ogrenciListesi.add(ogrenci);
        }
    } catch (SQLException e) {
        e.getMessage();
    }
    // Listeyi döndür
    return ogrenciListesi;
}


}

