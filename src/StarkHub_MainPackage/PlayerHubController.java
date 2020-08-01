package StarkHub_MainPackage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class PlayerHubController implements Initializable {
    public Label nameTxt;
    public Label channelNameTxt;
    public JFXTextArea commentTxt;
    public JFXListView<PojoToClientFlattened> commentList;
    public AnchorPane videoPlayer;
    public ProgressIndicator loading;
    public Label likeCount;
    public Label dislikeCount;
    public JFXButton likeBtn;
    public JFXButton dislikeBtn;
    public JFXButton watchLaterBtn;
    public AnchorPane rootPane;
    public JFXButton subscribeBtn;
    public JFXButton deleteBtn;

    private ObservableList<PojoToClientFlattened> comments;
    private FXMLLoader playerLoader;
    private JFXSnackbar snackbar;

    BufferedReader input;
    PrintWriter output;
    private String userName;
    private Gson gson;

    private int likeStatus,watchLaterStatus,subscribedStatus;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        snackbar=new JFXSnackbar(rootPane);
        input=MainHubController.input;
        output=MainHubController.output;
        userName=MainHubController.userName;
        gson = new GsonBuilder().serializeNulls().create();
        playerLoader=new FXMLLoader(getClass().getResource("ui/VideoPlayer.fxml"));
        commentList.setCellFactory(commentView -> new CommentListCellController());
        try {
            Parent rt=playerLoader.load();
            videoPlayer.getChildren().setAll(rt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInformation(String json){
        Gson gson = new GsonBuilder().serializeNulls().create();
        PojoToClient ptc=gson.fromJson(json,PojoToClient.class);
        ClientVideo cv=playerLoader.getController();
        try {
            cv.setServerIPAddr(InetAddress.getByName(ptc.getIPAddress()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if(ptc.getUserName().equals(MainHubController.userName)){
            deleteBtn.setVisible(true);
            deleteBtn.setDisable(false);
        }
        cv.setVideoFileName(ptc.getVideoPath().get(0));
        nameTxt.setText(ptc.getVideoName().get(0));
        channelNameTxt.setText(ptc.getChannelName().get(0));
        likeCount.setText(ptc.getVideoLikes().get(0).toString());
        dislikeCount.setText(ptc.getVideoDislikes().get(0).toString());
        likeStatus=ptc.getLikedDislikedStatus();
        if(likeStatus==1){
            likeBtn.setOpacity(0.75);
            likeBtn.setText("Liked!");
        }
        else if(likeStatus==-1)
            {
            dislikeBtn.setOpacity(0.75);
            dislikeBtn.setText("Disliked!");
        }
        watchLaterStatus=ptc.getIsWatchLater();
        if(ptc.getIsWatchLater()==1) {
            watchLaterBtn.setOpacity(0.75);
            watchLaterBtn.setText("Added to watch later");
        }
        subscribedStatus=ptc.getIsSubscribed();
        if(subscribedStatus==1){
            subscribeBtn.setOpacity(0.75);
            subscribeBtn.setText("Subscribed!");
        }
        comments= FXCollections.observableArrayList();
        ArrayList<String> commentsUser=ptc.getCommentUserName(),commentDate=ptc.getCommentCreationTime(),comment=ptc.getComment();
        for(int i=0;i<comment.size();i++){
            PojoToClientFlattened ptcf=new PojoToClientFlattened();
            ptcf.setComment(comment.get(i));
            ptcf.setCommentCreationTime(commentDate.get(i));
            ptcf.setCommentUserName(commentsUser.get(i));
            comments.add(ptcf);
        }
        commentList.setItems(comments);
        loading.setVisible(false);
    }
    public void onComment(){
        if(commentTxt.getText().isEmpty())
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Please write something before posting")));
        else{
            PojoFromClient pfc=new PojoFromClient();
            pfc.setVideoName(nameTxt.getText());
            pfc.setUserName(userName);
            pfc.setComment(commentTxt.getText());
            pfc.setChannelName(channelNameTxt.getText());
            pfc.setHeader(16);
            Task<String> task=new Task<String>() {
                @Override
                protected String call() throws Exception {
                    output.println(gson.toJson(pfc));
                    return input.readLine();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
                if(task.getValue().equals(LoginSignupController.sf)){
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Comment added successfully")));
                    PojoToClientFlattened ptcf=new PojoToClientFlattened();
                    ptcf.setCommentUserName(userName);
                    ptcf.setComment(pfc.getComment());
                    ptcf.setCommentCreationTime(new Timestamp(System.currentTimeMillis()).toString());
                    comments.add(ptcf);
                    commentTxt.clear();
                }
                else
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Some error occurred!")));
            });
            new Thread(task).start();
        }
    }
    public void onLike(){
        PojoFromClient pfc=new PojoFromClient();
        if(likeStatus==0){
            pfc.setHeader(14);
            pfc.setUserName(userName);
            pfc.setVideoName(nameTxt.getText());
            Task<String> task=new Task<String>() {
                @Override
                protected String call() throws Exception {
                    output.println(gson.toJson(pfc));
                    return input.readLine();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
                if(task.getValue().equals(LoginSignupController.sf)){
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("You liked this video!")));
                    likeBtn.setOpacity(0.75);
                    likeBtn.setText("Liked!");
                    likeCount.setText(Integer.toString(Integer.parseInt(likeCount.getText())+1));
                    likeStatus=1;
                }
                else
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Some error occurred")));
            });
            new Thread(task).start();
        }
        else if(likeStatus==1){
            pfc.setUserName(userName);
            pfc.setVideoName(nameTxt.getText());
            Task<String> task=new Task<String>() {
                @Override
                protected String call() throws Exception {
                    pfc.setHeader(24);
                    output.println(gson.toJson(pfc));
                    return input.readLine();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
                if(task.getValue().equals(LoginSignupController.sf)){
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("You removed like from this video!")));
                    likeBtn.setOpacity(1.0);
                    likeBtn.setText("Like!");
                    likeCount.setText(Integer.toString(Integer.parseInt(likeCount.getText())-1));
                    likeStatus=0;
                }
                else
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Some error occurred")));
            });
            new Thread(task).start();
        }
        else if(likeStatus==-1){
            pfc.setUserName(userName);
            pfc.setVideoName(nameTxt.getText());
            Task<String> task=new Task<String>() {
                @Override
                protected String call() throws Exception {
                    pfc.setHeader(25);
                    output.println(gson.toJson(pfc));
                    input.readLine();
                    pfc.setHeader(14);
                    output.println(gson.toJson(pfc));
                    return input.readLine();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
                if(task.getValue().equals(LoginSignupController.sf)){
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("You liked this video!")));
                    likeBtn.setOpacity(0.75);
                    likeBtn.setText("Liked!");
                    dislikeBtn.setOpacity(1.0);
                    dislikeBtn.setText("Dislike!");
                    likeCount.setText(Integer.toString(Integer.parseInt(likeCount.getText())+1));
                    dislikeCount.setText(Integer.toString(Integer.parseInt(dislikeCount.getText())-1));
                    likeStatus=1;
                }
                else
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Some error occurred")));
            });
            new Thread(task).start();
        }

    }
    public void onDislike(){
        PojoFromClient pfc=new PojoFromClient();
        if(likeStatus==0){
            pfc.setHeader(15);
            pfc.setUserName(userName);
            pfc.setVideoName(nameTxt.getText());
            Task<String> task=new Task<String>() {
                @Override
                protected String call() throws Exception {
                    output.println(gson.toJson(pfc));
                    return input.readLine();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
                if(task.getValue().equals(LoginSignupController.sf)){
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("You disliked this video!")));
                    dislikeBtn.setOpacity(0.75);
                    dislikeBtn.setText("Disliked!");
                    dislikeCount.setText(Integer.toString(Integer.parseInt(dislikeCount.getText())+1));
                    likeStatus=-1;
                }
                else
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Some error occurred")));
            });
            new Thread(task).start();
        }
        else if(likeStatus==-1){
            pfc.setUserName(userName);
            pfc.setVideoName(nameTxt.getText());
            Task<String> task=new Task<String>() {
                @Override
                protected String call() throws Exception {
                    pfc.setHeader(25);
                    output.println(gson.toJson(pfc));
                    return input.readLine();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
                if(task.getValue().equals(LoginSignupController.sf)){
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("You removed dislike from this video!")));
                    dislikeBtn.setOpacity(1.0);
                    dislikeBtn.setText("Dislike!");
                    dislikeCount.setText(Integer.toString(Integer.parseInt(dislikeCount.getText())-1));
                    likeStatus=0;
                }
                else
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Some error occurred")));
            });
            new Thread(task).start();
        }
        else if(likeStatus==1){
            pfc.setUserName(userName);
            pfc.setVideoName(nameTxt.getText());
            Task<String> task=new Task<String>() {
                @Override
                protected String call() throws Exception {
                    pfc.setHeader(24);
                    output.println(gson.toJson(pfc));
                    input.readLine();
                    pfc.setHeader(15);
                    output.println(gson.toJson(pfc));
                    return input.readLine();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
                if(task.getValue().equals(LoginSignupController.sf)){
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("You disliked this video!")));
                    dislikeBtn.setOpacity(0.75);
                    dislikeBtn.setText("Disliked!");
                    likeBtn.setOpacity(1.0);
                    likeBtn.setText("Like!");
                    dislikeCount.setText(Integer.toString(Integer.parseInt(dislikeCount.getText())+1));
                    likeCount.setText(Integer.toString(Integer.parseInt(likeCount.getText())-1));
                    likeStatus=-1;
                }
                else
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Some error occurred")));
            });
            new Thread(task).start();
        }

    }
    public void onWatchLater(){
        PojoFromClient pfc=new PojoFromClient();
        if(watchLaterStatus==0){
            pfc.setHeader(19);
            pfc.setUserName(userName);
            pfc.setChannelName(channelNameTxt.getText());
            pfc.setVideoName(nameTxt.getText());
            Task<String> task=new Task<String>() {
                @Override
                protected String call() throws Exception {
                    output.println(gson.toJson(pfc));
                    return input.readLine();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
                if(task.getValue().equals(LoginSignupController.sf)){
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Video was added to your Watch Later!")));
                    watchLaterBtn.setOpacity(0.75);
                    watchLaterBtn.setText("Added to watch later");
                    watchLaterStatus=1;
                }
                else
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Some error occurred")));
            });
            new Thread(task).start();
        }
        else if(watchLaterStatus==1){
            pfc.setHeader(20);
            pfc.setUserName(userName);
            pfc.setVideoName(nameTxt.getText());
            Task<String> task=new Task<String>() {
                @Override
                protected String call() throws Exception {
                    output.println(gson.toJson(pfc));
                    return input.readLine();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
                if(task.getValue().equals(LoginSignupController.sf)){
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Video was removed from your Watch Later!")));
                    watchLaterBtn.setOpacity(1.0);
                    watchLaterBtn.setText("Add to watch later");
                    watchLaterStatus=0;
                }
                else
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Some error occurred")));
            });
            new Thread(task).start();
        }
    }
    public void onSubscribe(){
        PojoFromClient pfc=new PojoFromClient();
        if(subscribedStatus==0){
            pfc.setHeader(17);
            pfc.setUserName(userName);
            pfc.setChannelName(channelNameTxt.getText());
            Task<String> task=new Task<String>() {
                @Override
                protected String call() throws Exception {
                    output.println(gson.toJson(pfc));
                    return input.readLine();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
                if(task.getValue().equals(LoginSignupController.sf)){
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("You subscribed to "+channelNameTxt.getText()+"!")));
                    subscribeBtn.setOpacity(0.75);
                    subscribeBtn.setText("Subscribed!");
                    subscribedStatus=1;
                }
                else
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Some error occurred")));
            });
            new Thread(task).start();
        }
        else if(subscribedStatus==1){
            pfc.setHeader(18);
            pfc.setUserName(userName);
            pfc.setChannelName(channelNameTxt.getText());
            Task<String> task=new Task<String>() {
                @Override
                protected String call() throws Exception {
                    output.println(gson.toJson(pfc));
                    return input.readLine();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
                if(task.getValue().equals(LoginSignupController.sf)){
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("You unsubscribed from "+channelNameTxt.getText()+"!")));
                    subscribeBtn.setOpacity(1.0);
                    subscribeBtn.setText("Subscribe!");
                    subscribedStatus=0;
                }
                else
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Some error occurred")));
            });
            new Thread(task).start();
        }
    }
    public void onDelete(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Deleting "+nameTxt.getText());
        alert.setContentText("Are you sure you want to delete this video?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            PojoFromClient pfc=new PojoFromClient();
            pfc.setHeader(23);
            pfc.setVideoName(nameTxt.getText());
            pfc.setChannelName(channelNameTxt.getText());
            Task<String> task=new Task<String>() {
                @Override
                protected String call() throws Exception {
                    output.println(gson.toJson(pfc));
                    return input.readLine();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
                if(task.getValue().equals(LoginSignupController.sf))
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("This video is now deleted. Reload page to view changes")));
                 else
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Some error occurred")));
                });
            new Thread(task).start();
        }

    }

}
