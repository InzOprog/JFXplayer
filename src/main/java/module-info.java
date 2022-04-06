module com.example.jfxplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires jid3lib;


    opens com.example.jfxplayer to javafx.fxml;
    exports com.example.jfxplayer;
}