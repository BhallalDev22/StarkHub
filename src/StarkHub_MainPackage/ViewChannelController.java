package StarkHub_MainPackage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ViewChannelController implements Initializable {

    public Pane infoPane;
    public ProgressIndicator channelInfoProgress;
    public ProgressIndicator videoListProgress;
    public Label channelNameTxt;
    public Label creationTimeTxt;
    public Label videoCountTxt;
    public Label subCountTxt;
    public JFXButton uploadVideoBtn;
    public JFXButton newChannelBtn;
    @FXML
    JFXListView<PojoToClientFlattened> channelList,videoList;

    private AnchorPane content;

    private ObservableList<PojoToClientFlattened> ptfc;
    private JFXButton trendingBtn;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        channelList.setItems(ptfc);
        channelList.setCellFactory(channelListView -> new ChannelListCellController());
        videoList.setCellFactory(videoListView -> new VideoListCellController());
        infoPane.setVisible(false);
        channelList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PojoToClientFlattened>() {
            @Override
            public void changed(ObservableValue<? extends PojoToClientFlattened> observableValue, PojoToClientFlattened pojoToClientFlattened, PojoToClientFlattened t1) {
                infoPane.setVisible(false);
                channelInfoProgress.setVisible(true);
                videoListProgress.setVisible(true);
                BufferedReader input=MainHubController.input;
                PrintWriter output=MainHubController.output;
                String userName=MainHubController.userName;
                PojoFromClient pfc=new PojoFromClient();
                Gson gson = new GsonBuilder().serializeNulls().create();

                pfc.setChannelName(t1.getChannelName());
                Task<ArrayList<String>> task=new Task<ArrayList<String>>() {
                    @Override
                    protected ArrayList<String> call() throws Exception {
                        ArrayList<String> st=new ArrayList<>();
                        pfc.setHeader(7);
                        output.println(gson.toJson(pfc));
                        st.add(input.readLine());
                        pfc.setHeader(8);
                        output.println(gson.toJson(pfc));
                        st.add(input.readLine());
                        return st;
                    }
                };


                task.setOnSucceeded(workerStateEvent -> {
                    channelInfoProgress.setVisible(false);
                    videoListProgress.setVisible(false);

                    if(!(task.getValue().get(0).equals(LoginSignupController.usf))){

                        PojoToClient ptc=gson.fromJson(task.getValue().get(0),PojoToClient.class);
                        ArrayList<String> channelNames=ptc.getChannelName();
                        ArrayList<Integer> subcounts=ptc.getNumberOfSubscribers();
                        ArrayList<Integer>  vidCount=ptc.getNumberOfVideos();
                        ArrayList<String>  creationTime=ptc.getChannelCreationTime();
                        channelNameTxt.setText(channelNames.get(0));
                        subCountTxt.setText(subcounts.get(0).toString());
                        videoCountTxt.setText(vidCount.get(0).toString());
                        creationTimeTxt.setText(creationTime.get(0));
                        infoPane.setVisible(true);
                    }

                       if(!(task.getValue().get(1).equals(LoginSignupController.usf))) {
                           ObservableList<PojoToClientFlattened> observableList = FXCollections.observableArrayList();
                           PojoToClient ptc = gson.fromJson(task.getValue().get(1), PojoToClient.class);
                           ArrayList<String> videoNames = ptc.getVideoName();
                           ArrayList<Integer> likes = ptc.getVideoLikes();
                           ArrayList<Integer> dislikes = ptc.getVideoDislikes();
                           ArrayList<Integer> views = ptc.getVideoViews();
                           for (int i = 0; i < videoNames.size(); i++) {
                               PojoToClientFlattened ptcf = new PojoToClientFlattened();
                               ptcf.setChannelName(channelList.getSelectionModel().getSelectedItem().getChannelName());
                               ptcf.setVideoName(videoNames.get(i));
                               ptcf.setVideoLikes(likes.get(i));
                               ptcf.setVideoDislikes(dislikes.get(i));
                               ptcf.setVideoViews(views.get(i));
                               observableList.add(ptcf);
                           }
                           videoList.setItems(observableList);
                       }

                });



                new Thread(task).start();

            }
        });
        videoList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PojoToClientFlattened>() { //Select a video and start playing it
            @Override
            public void changed(ObservableValue<? extends PojoToClientFlattened> observableValue, PojoToClientFlattened pojoToClientFlattened, PojoToClientFlattened t1) {
                MainHubController.loadPlayer(content,t1.getVideoName());
            }
        });
    }

    public void onCreateNewChannel() throws IOException {
        Parent root1= FXMLLoader.load(getClass().getResource("NewChannelDialog.fxml"));
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root1));
        stage.show();
        trendingBtn.fire();
    }

    public void setPtfc(ObservableList<PojoToClientFlattened> ptfc) {
        this.ptfc = ptfc;
        channelList.setItems(ptfc);
    }

    public void setTrendingBtn(JFXButton trendingBtn) {
        this.trendingBtn = trendingBtn;
    }

    public void onUploadVideo() throws IOException{
        FXMLLoader loader= new FXMLLoader(getClass().getResource("UploadVideoDialog.fxml"));
        Parent root1=loader.load();
        UploadVideoDialogController uvdc=loader.getController();
        uvdc.setChannelName(channelList.getSelectionModel().getSelectedItem().getChannelName());
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root1));
        stage.show();
        trendingBtn.fire();
    }

    public void setContent(AnchorPane content) {
        this.content = content;
    }
    public void videoUploadable(boolean flag){
        if(!flag)
            uploadVideoBtn.setDisable(true);
            newChannelBtn.setDisable(true);
    }
}
