package main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionFactory {
    private static final String DB_FILE = "jdbc:sqlite:ogrenciler.db";

    static {
        try {
            // SQLite JDBC sürücüsünü yükle
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC sürücüsü bulunamadı!", e);
        }
    }

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(DB_FILE);
            createTablesIfNotExist(connection);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Veritabanına bağlanılamadı! " + e.getMessage(), e);
        }
    }

    private static void createTablesIfNotExist(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS OGRENCILER ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "adSoyad TEXT NOT NULL, "
                        + "cinsiyet TEXT NOT NULL"
                        + ");";
        
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Tablo oluşturulurken bir hata oluştu!", e);
        }
    }
}

