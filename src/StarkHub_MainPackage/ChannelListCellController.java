package StarkHub_MainPackage;

import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ChannelListCellController extends JFXListCell<PojoToClientFlattened> {
    @FXML
    Label subcount,name,label;
    @FXML
    VBox rootPane;

    private FXMLLoader loader;

    @Override
    protected void updateItem(PojoToClientFlattened item,boolean empty){
        super.updateItem(item,empty);
        if(empty||item==null){}
        else{
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("ChannelListCell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            name.setText(item.getChannelName());
            if(item.getNumberOfSubscribers()!=null)
            subcount.setText(item.getNumberOfSubscribers().toString());
            else if(item.getSubscribeTime()!=null){
                label.setText("Subscribed on :");
                subcount.setText(item.getSubscribeTime());
            }

            setText(null);
            setGraphic(rootPane);
            }
        }
    }

