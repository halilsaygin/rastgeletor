module com.rastgeletor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    
    opens main to javafx.fxml;
    opens main.controllers to javafx.fxml;
    opens main.database to javafx.fxml;
    
    exports main;
    exports main.controllers;
    exports main.database;
    exports main.utils;
    exports main.dialogboxes;
}
