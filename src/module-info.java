module StarkHub.Peer {
    requires javafx.media;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires com.jfoenix;
    requires java.logging;
    requires java.desktop;
    requires org.bytedeco.javacv;
    requires org.bytedeco.ffmpeg;
    requires org.bytedeco.javacv.platform;
    requires org.bytedeco.javacpp;
    requires gson;
    requires java.sql;

    opens StarkHub_MainPackage;
}
