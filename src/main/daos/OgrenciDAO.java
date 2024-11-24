
package main.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Ogrenci;

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
    
    // Öğrenci listesi sorgulama
    public ObservableList<Ogrenci> ogrenciListesiGetir() throws SQLException{
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
}
