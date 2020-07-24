package StarkHub_MainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main2 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            FXMLLoader loader =new FXMLLoader(getClass().getResource("VideoPlayer.fxml"));
            Parent root=loader.load();
            ClientVideo cv=loader.getController();
            cv.setVideoFileName("/home/arcgeekz/development/IdeaProjects/StarkHub/src/Graphics/TEST2.mkv");
            cv.setServerIPAddr(InetAddress.getByName("localhost"));
            cv.initVideo();
            stage.setTitle("Player");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
