module com.example.proxectofct {
    requires javafx.controls;
    requires javafx.fxml;


    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires java.persistence;
    requires java.validation;
    requires org.hibernate.orm.core;

    requires javax.mail;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires itextpdf;
    requires jfxtras.controls;


    opens inicio to javafx.fxml;
    exports inicio;
    exports controllers;
    exports modelos;
    opens modelos;
    opens controllers to javafx.fxml;
}