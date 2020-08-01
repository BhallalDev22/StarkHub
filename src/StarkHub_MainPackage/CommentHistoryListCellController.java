package StarkHub_MainPackage;

import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class CommentHistoryListCellController extends JFXListCell<PojoToClientFlattened> {
    @FXML
    Label nameTxt,channelNameTxt,commentTxt,timeTxt;
    @FXML
    HBox rootPane;

    private FXMLLoader loader;

    @Override
    protected void updateItem(PojoToClientFlattened item,boolean empty){
        super.updateItem(item,empty);
        if(empty||item==null){}
        else{
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("ui/CommentHistoryListCell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            nameTxt.setText(item.getVideoName());
            channelNameTxt.setText(item.getChannelName());
            commentTxt.setText(item.getComment());
            timeTxt.setText(item.getCommentCreationTime());
            setText(null);
            setGraphic(rootPane);
        }
    }
}

