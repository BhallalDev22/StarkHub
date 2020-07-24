package StarkHub_MainPackage;

import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class VideoListCell2Controller extends JFXListCell<PojoToClientFlattened> {
    @FXML
    Label nameTxt,channelNameTxt,viewsTxt,viewLabel;
    @FXML
    HBox rootPane;

    private FXMLLoader loader;

    @Override
    protected void updateItem(PojoToClientFlattened item,boolean empty){
        super.updateItem(item,empty);
        if(empty||item==null){}
        else{
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("VideoListCell2.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            nameTxt.setText(item.getVideoName());
            channelNameTxt.setText(item.getChannelName());
            if(item.getVideoViews()!=null) {
                viewsTxt.setText(item.getVideoViews().toString());
            }
            else if(item.getWatchLaterTime()!=null){
                viewsTxt.setText(item.getWatchLaterTime());
                viewLabel.setText("Added on : ");
            }
            else if(item.getWatchTime()!=null){
                viewsTxt.setText(item.getWatchTime());
                viewLabel.setText("Last watched : ");
            }
            else{
                viewsTxt.setVisible(false);
                viewLabel.setVisible(false);
            }
            setText(null);
            setGraphic(rootPane);
        }
    }
}

