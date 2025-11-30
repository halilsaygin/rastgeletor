package main.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.database.Ogrenci;

public class OgrenciDAO {

    private final Connection connection;

    public OgrenciDAO(Connection connection) {
        this.connection = connection;
    }

    // Öğrenci Ekleme metodu
    public void ogrenciEkle(String adSoyad, String cinsiyet) throws SQLException {
        String sql = "INSERT INTO OGRENCILER (adSoyad, cinsiyet) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, adSoyad);
        preparedStatement.setString(2, cinsiyet);
        preparedStatement.executeUpdate();
    }

    // Öğrenci silme metodu
    public void ogrenciSil(int ogrenciId) throws SQLException {
        String sql = "DELETE FROM OGRENCILER WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, ogrenciId);
        preparedStatement.executeUpdate();
    }
    
    public void tumKayitlariSil() throws SQLException{
        String sql = "DELETE FROM OGRENCILER";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.executeUpdate();
    }

    // Tüm öğrenci  listesi sorgulama
    public ObservableList<Ogrenci> ogrenciListesiGetir() throws SQLException {
        String sql = "SELECT * FROM OGRENCILER";
        ObservableList<Ogrenci> ogrenciListesi = FXCollections.observableArrayList();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String adSoyad = resultSet.getString("adSoyad");
            String cinsiyet = resultSet.getString("cinsiyet");

            Ogrenci ogrenci = new Ogrenci(id, adSoyad, cinsiyet);
            ogrenciListesi.add(ogrenci);
        }

        return ogrenciListesi;
    }
    
    // erkek öğrencileri sorgulama
    public ObservableList<Ogrenci> erkek_ogrenciListesiGetir() throws SQLException {
        String sql = "SELECT * FROM OGRENCILER WHERE cinsiyet=?";
        ObservableList<Ogrenci>  erkek_ogrenciListesi = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "Erkek");
        ResultSet resultSet = preparedStatement.executeQuery(); 
        
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String adSoyad = resultSet.getString("adSoyad");
            String cinsiyet = resultSet.getString("cinsiyet");

            Ogrenci ogrenci = new Ogrenci(id, adSoyad, cinsiyet);
            erkek_ogrenciListesi.add(ogrenci);
        }

        return erkek_ogrenciListesi;
    }
    
    // kız öğrencileri sorgulama
    public ObservableList<Ogrenci> kiz_ogrenciListesiGetir() throws SQLException {
        String sql = "SELECT * FROM OGRENCILER WHERE cinsiyet=?";
        ObservableList<Ogrenci>  kiz_ogrenciListesi = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "Kız");
        ResultSet resultSet = preparedStatement.executeQuery(); 
        
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String adSoyad = resultSet.getString("adSoyad");
            String cinsiyet = resultSet.getString("cinsiyet");

            Ogrenci ogrenci = new Ogrenci(id, adSoyad, cinsiyet);
            kiz_ogrenciListesi.add(ogrenci);
        }

        return kiz_ogrenciListesi;
    }
    
}
