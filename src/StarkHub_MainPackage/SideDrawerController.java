package StarkHub_MainPackage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SideDrawerController implements Initializable {

    @FXML
    private JFXButton trendingBtn, viewChannelBtn, watchLaterBtn, watchHistoryBtn, accountBtn,viewSubChannelBtn,commentHistoryBtn;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }



    public JFXButton getTrendingBtn() {
        return trendingBtn;
    }

    public JFXButton getViewChannelBtn() {
        return viewChannelBtn;
    }

    public JFXButton getWatchLaterBtn() {
        return watchLaterBtn;
    }

    public JFXButton getWatchHistoryBtn() {
        return watchHistoryBtn;
    }

    public JFXButton getAccountBtn() {
        return accountBtn;
    }

    public JFXButton getViewSubChannelBtn() {
        return viewSubChannelBtn;
    }

    public JFXButton getCommentHistoryBtn() {
        return commentHistoryBtn;
    }
}

