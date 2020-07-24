package StarkHub_MainPackage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class NewChannelDialogController implements Initializable {
    public JFXTextField channelNameTxt;
    public JFXPasswordField passwordTxt;
    public AnchorPane anchorPane;

    private Stage progress;
    private JFXSnackbar snackbar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        snackbar = new JFXSnackbar(anchorPane);
    }

    public void onCancel(){
        ((Stage)channelNameTxt.getScene().getWindow()).close();
    }
    public void onSubmit() throws IOException {
        BufferedReader input=MainHubController.input;
        PrintWriter output=MainHubController.output;
        String userName=MainHubController.userName;
        PojoFromClient pfc=new PojoFromClient();
        Gson gson = new GsonBuilder().serializeNulls().create();
        pfc.setHeader(9);
        pfc.setUserName(userName);
        pfc.setPassword(passwordTxt.getText());
        pfc.setChannelName(channelNameTxt.getText());
        Task<String> task=new Task<String>() {
            @Override
            protected String call() throws Exception {
                output.println(gson.toJson(pfc));
                return input.readLine();
            }
        };
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("progress.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        progress = new Stage(StageStyle.UNDECORATED);
        progress.initModality(Modality.WINDOW_MODAL);
        progress.setScene(new Scene(root1));
        progress.setAlwaysOnTop(true);
        progress.show();
        task.setOnSucceeded(workerStateEvent -> {
            progress.close();
            if(task.getValue().equals(LoginSignupController.usf)){
                snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Invalid password")));
            }
            else{
                onCancel();
            }
        });
        Thread thread=new Thread(task);
        thread.start();
    }
}
