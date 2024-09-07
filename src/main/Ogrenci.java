package main;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

public class Ogrenci {
    
    private final IntegerProperty id;
    private final StringProperty adSoyad;
    private final StringProperty cinsiyet;
    
    public Ogrenci(SimpleIntegerProperty id,StringProperty adSoyad, StringProperty cinsiyet) {
        this.id = id;
        this.adSoyad = adSoyad;
        this.cinsiyet = cinsiyet;
    }

    public IntegerProperty idProperty() {
    return id;
}
    public StringProperty adSoyadProperty(){
        return this.adSoyad;
    }
    
    public StringProperty cinsiyetProperty(){
        return this.cinsiyet;
    }
}

