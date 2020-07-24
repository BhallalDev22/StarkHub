package StarkHub_MainPackage;

import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class VideoListCellController extends JFXListCell<PojoToClientFlattened> {
    @FXML
    Label nameTxt,channelNameTxt,likesTxt,dislikesTxt,viewsTxt;
    @FXML
    HBox rootPane;

    private FXMLLoader loader;

    @Override
    protected void updateItem(PojoToClientFlattened item,boolean empty){
        super.updateItem(item,empty);
        if(empty||item==null){}
        else{
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("VideoListCell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            nameTxt.setText(item.getVideoName());
            channelNameTxt.setText(item.getChannelName());
            likesTxt.setText(item.getVideoLikes().toString());
            dislikesTxt.setText(item.getVideoDislikes().toString());
            viewsTxt.setText(item.getVideoViews().toString());
            setText(null);
            setGraphic(rootPane);
        }
    }
}

