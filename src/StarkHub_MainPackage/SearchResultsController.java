package StarkHub_MainPackage;

import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

public class SearchResultsController implements Initializable {
    public JFXListView<PojoToClientFlattened> list;

    private AnchorPane content;
    private String targetTxt;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PojoToClientFlattened>() { //Select a video and start playing it
            @Override
            public void changed(ObservableValue<? extends PojoToClientFlattened> observableValue, PojoToClientFlattened pojoToClientFlattened, PojoToClientFlattened t1) {
                MainHubController.loadPlayer(content,t1.getVideoName());
            }
        });
    }

    public void setResultsList(PojoToClient ptc) {
        list.setCellFactory(whlFactory -> new VideoListCell2Controller());
        ArrayList<PojoToClientFlattened> videos=new ArrayList<>();
        ArrayList<String> videoNames=ptc.getVideoName();
        for(int i=0;i<videoNames.size();i++){
            PojoToClientFlattened ptcf=new PojoToClientFlattened();
            ptcf.setVideoName(videoNames.get(i));
            ptcf.setLevDistance(levenshteinDistance(videoNames.get(i),targetTxt));
            videos.add(ptcf);
        }
        videos.sort(Comparator.comparing(PojoToClientFlattened::getLevDistance));
        ObservableList<PojoToClientFlattened> observableList= FXCollections.observableArrayList();
        observableList.setAll(videos);
        list.setItems(observableList);
    }
    public int levenshteinDistance (CharSequence lhs, CharSequence rhs) {  //Algorithm used to order the search results
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++) cost[i] = i;

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for(int i = 1; i < len0; i++) {
                // matching current letters in both strings
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert  = cost[i] + 1;
                int cost_delete  = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost; cost = newcost; newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
    }

    public void setContent(AnchorPane content) {
        this.content=content;
    }

    public void setTargetTxt(String targetTxt) {
        this.targetTxt=targetTxt;
    }
}

