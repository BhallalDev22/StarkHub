package StarkHub_MainPackage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainHubController implements Initializable{

    @FXML
    private AnchorPane pane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private Label helloTxt;


    private String trending,notifications,userName;
    private Socket sock;
    private BufferedReader input;
    private PrintWriter output;
    private Stage progress;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("SideDrawer.fxml"));
            VBox box = loader.load();
//            SideDrawerController controller = loader.getController();
//            controller.setCallback(this);
            drawer.setSidePane(box);
        } catch (IOException ex) {
            Logger.getLogger(MainHubController.class.getName()).log(Level.SEVERE, null, ex);
        }
        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
        transition.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();

            if (drawer.isOpened()) {
                drawer.close();
            } else {
                drawer.open();
            }
        });
    }

    public void setTrending(String trending){
        this.trending=trending;
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }


    public void setUserName(String userName) {
        this.userName = userName;
        helloTxt.setText("Hello, "+userName+" !");
    }

    public void setSock(Socket sock) {
        this.sock = sock;
        try {
            input = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            output = new PrintWriter(sock.getOutputStream(), true);
        }catch (IOException e){e.printStackTrace();}
    }

    public void onClose() throws Exception{
        PojoFromClient pfc=new PojoFromClient();
        Gson gson = new GsonBuilder().serializeNulls().create();
        System.out.println(userName);
        pfc.setHeader(21);
        pfc.setUserName(userName);
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
            System.out.println(task.getValue());
            Platform.exit();
        });
        Thread thread=new Thread(task);
        thread.start();
    }
}
