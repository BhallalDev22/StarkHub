package StarkHub_MainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MainHub.fxml"));
            stage.setTitle("StarkHub Login Window");
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
