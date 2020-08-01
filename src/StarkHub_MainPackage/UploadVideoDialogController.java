package StarkHub_MainPackage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class UploadVideoDialogController implements Initializable {

    public Label videoPath;
    public AnchorPane anchorPane;
    @FXML
    JFXTextField videoNameTxt,tagsTxt;

    private Stage progress;
    private File videoFile;
    private FileChooser fileChooser;
    private JFXSnackbar snackbar;
    private String channelName;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileChooser=new FileChooser();
        snackbar = new JFXSnackbar(anchorPane);
    }

    public void onVideoSelect(){
        videoFile=fileChooser.showOpenDialog(tagsTxt.getScene().getWindow());
        videoPath.setText(videoFile.getAbsolutePath());
        System.out.println(videoPath.getText());
    }

    public void onCancel(){
        ((Stage)tagsTxt.getScene().getWindow()).close();
    }

    public void onSubmit() throws IOException {
        if (videoNameTxt.getText().isEmpty() || tagsTxt.getText().isEmpty() || videoFile == null) {
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Please fill all details")));
        } else {
            BufferedReader input = MainHubController.input;
            PrintWriter output = MainHubController.output;
            String userName = MainHubController.userName;
            PojoFromClient pfc = new PojoFromClient();
            Gson gson = new GsonBuilder().serializeNulls().create();
            pfc.setHeader(10);
            pfc.setUserName(userName);
            pfc.setVideoPath(videoPath.getText());
            pfc.setVideoName(videoNameTxt.getText());
            pfc.setVideoTag(tagsTxt.getText());
            pfc.setChannelName(channelName);
            Task<String> task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    output.println(gson.toJson(pfc));
                    return input.readLine();
                }
            };
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui/progress.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            progress = new Stage(StageStyle.UNDECORATED);
            progress.initModality(Modality.WINDOW_MODAL);
            progress.setScene(new Scene(root1));
            progress.setAlwaysOnTop(true);
            progress.show();
            task.setOnSucceeded(workerStateEvent -> {
                progress.close();
                if (task.getValue().equals(LoginSignupController.usf)) {
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Some error occurred")));
                } else {
                    onCancel();
                }
            });
            Thread thread = new Thread(task);
            thread.start();
        }
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
