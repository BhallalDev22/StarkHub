package StarkHub_MainPackage;

import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class CommentHistoryController implements Initializable {
    public JFXListView<PojoToClientFlattened> list;

    private AnchorPane content;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PojoToClientFlattened>() { //Select a video and start playing it
            @Override
            public void changed(ObservableValue<? extends PojoToClientFlattened> observableValue, PojoToClientFlattened pojoToClientFlattened, PojoToClientFlattened t1) {
                MainHubController.loadPlayer(content,t1.getVideoName());
            }
        });
    }

    public void setCommentHistoryList(ObservableList<PojoToClientFlattened> commentHistoryList) {
        list.setCellFactory(chlFactory -> new CommentHistoryListCellController());
        list.setItems(commentHistoryList);
    }

    public void setContent(AnchorPane content) {
        this.content=content;
    }
}
