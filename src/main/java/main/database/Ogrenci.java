package main.database;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Ogrenci {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty adSoyad;
    private final SimpleStringProperty cinsiyet;

    // Constructor
    public Ogrenci(int id, String adSoyad, String cinsiyet) {
        this.id = new SimpleIntegerProperty(id);
        this.adSoyad = new SimpleStringProperty(adSoyad);
        this.cinsiyet = new SimpleStringProperty(cinsiyet);
    }

    // Getter ve Setter'lar
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getAdSoyad() {
        return adSoyad.get();
    }

    public void setAdSoyad(String adSoyad) {
        this.adSoyad.set(adSoyad);
    }

    public String getCinsiyet() {
        return cinsiyet.get();
    }

    public void setCinsiyet(String cinsiyet) {
        this.cinsiyet.set(cinsiyet);
    }
}
