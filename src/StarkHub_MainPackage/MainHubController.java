package StarkHub_MainPackage;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

public class MainHubController implements Initializable{

    @FXML
    private AnchorPane pane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;

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

}
