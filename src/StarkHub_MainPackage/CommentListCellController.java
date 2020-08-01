package StarkHub_MainPackage;

import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CommentListCellController extends JFXListCell<PojoToClientFlattened> {
    public Label name;
    public Label time;
    public Label comment;
    public AnchorPane rootPane;

    private FXMLLoader loader;

    @Override
    protected void updateItem(PojoToClientFlattened item,boolean empty){
        super.updateItem(item,empty);
        if(empty||item==null){}
        else{
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("ui/CommentListCell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            name.setText(item.getCommentUserName());
            time.setText(item.getCommentCreationTime());
            comment.setText(item.getComment());

            setText(null);
            setGraphic(rootPane);
        }
    }
}
