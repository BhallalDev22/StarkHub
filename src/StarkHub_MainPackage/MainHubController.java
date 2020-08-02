package StarkHub_MainPackage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainHubController implements Initializable{

    public JFXTextField searchbar;
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private Label helloTxt;
    @FXML
    private Label headingTxt;

    @FXML
    public AnchorPane content;


    private String trending,notifications;
    private Socket sock;
    public static String userName;
    public static BufferedReader input;
    public static PrintWriter output;
    private Stage progress;
    private JFXButton trendingBtn,viewChannelBtn,watchLaterBtn, watchHistoryBtn, accountBtn,subChannelBtn,commentHistoryBtn;
    public static int currentScreen=-1;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        try {


            Thread serverFactory=new Thread(new ServerFactory());
            serverFactory.setDaemon(true);
            serverFactory.start();
            searchbar.setOnKeyPressed(ke -> {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                    if(!(searchbar.getText().isEmpty())){
                        currentScreen=-1;
                        headingTxt.setText("Search results...");
                        try {
                            FXMLLoader loader=new FXMLLoader((getClass().getResource("ui/SearchResults.fxml")));
                            Parent rt = loader.load();

                            SearchResultsController src=loader.getController();
                            src.setContent(content);
                            src.setTargetTxt(searchbar.getText());
                            PojoFromClient pfc=new PojoFromClient();
                            Gson gson = new GsonBuilder().serializeNulls().create();
                            pfc.setHeader(3);
                            Task<String> task=new Task<String>() {
                                @Override
                                protected String call() throws Exception {
                                    output.println(gson.toJson(pfc));
                                    return input.readLine();
                                }
                            };
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui/progress.fxml"));
                            Parent root1 = (Parent) fxmlLoader.load();
                            progress = new Stage(StageStyle.UNDECORATED);
                            progress.initModality(Modality.WINDOW_MODAL);
                            progress.setScene(new Scene(root1));
                            progress.setAlwaysOnTop(true);
                            progress.show();
                            task.setOnSucceeded(workerStateEvent -> {
                                progress.close();
                                PojoToClient ptc=gson.fromJson(task.getValue(),PojoToClient.class);
                                src.setResultsList(ptc);
                            });
                            Thread thread=new Thread(task);
                            thread.start();
                            content.getChildren().setAll(rt);
                            searchbar.setText("");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/SideDrawer.fxml"));
            VBox box = loader.load();
            SideDrawerController sdc=loader.getController();

            trendingBtn=sdc.getTrendingBtn();
            viewChannelBtn=sdc.getViewChannelBtn();
            watchLaterBtn=sdc.getWatchLaterBtn();
            watchHistoryBtn=sdc.getWatchHistoryBtn();
            accountBtn=sdc.getAccountBtn();
            subChannelBtn=sdc.getViewSubChannelBtn();
            commentHistoryBtn=sdc.getCommentHistoryBtn();

            trendingBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (currentScreen != 0) {
                        headingTxt.setText("Trending...");

                        try {
                            Gson gson = new GsonBuilder().serializeNulls().create();
                            VaryingData vd=gson.fromJson(trending,VaryingData.class);
                            ObservableList<PojoToClientFlattened> trendingList=FXCollections.observableArrayList();
                            //Convert VaryingData to PojoToClientFlattened
                            ArrayList<String> vidName=vd.getVideoName();
                            ArrayList<String> channelName=vd.getChannelName();
                            ArrayList<Integer> views=vd.getVideoViews();
                            for(int i=0;i<vidName.size();i++){
                                PojoToClientFlattened ptcf=new PojoToClientFlattened();
                                ptcf.setVideoName(vidName.get(i));
                                ptcf.setChannelName(channelName.get(i));
                                ptcf.setVideoViews(views.get(i));
                                trendingList.add(ptcf);
                            }

                            FXMLLoader loader1=new FXMLLoader((getClass().getResource("ui/TrendingList.fxml")));
                            Parent rt=loader1.load();
                            TrendingListController tlc=loader1.getController();
                            tlc.setTrendingList(trendingList);
                            tlc.setContent(content);
                            content.getChildren().setAll(rt);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currentScreen = 0;
                        drawer.close();
                        drawer.toBack();
                    }
                }
            });

            viewChannelBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (currentScreen != 1) {
                        headingTxt.setText("Channels...");

                        try {
                            FXMLLoader loader=new FXMLLoader((getClass().getResource("ui/ViewChannel.fxml")));
                            Parent rt = loader.load();

                            ViewChannelController vcc=loader.getController();
                            vcc.setTrendingBtn(trendingBtn);
                            vcc.setContent(content);
                            PojoFromClient pfc=new PojoFromClient();
                            Gson gson = new GsonBuilder().serializeNulls().create();
                            pfc.setHeader(6);
                            pfc.setUserName(userName);
                            Task<String> task=new Task<String>() {
                                @Override
                                protected String call() throws Exception {
                                    output.println(gson.toJson(pfc));
                                    return input.readLine();
                                }
                            };
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui/progress.fxml"));
                            Parent root1 = (Parent) fxmlLoader.load();
                            progress = new Stage(StageStyle.UNDECORATED);
                            progress.initModality(Modality.WINDOW_MODAL);
                            progress.setScene(new Scene(root1));
                            progress.setAlwaysOnTop(true);
                            progress.show();
                            task.setOnSucceeded(workerStateEvent -> {
                                progress.close();
                                ObservableList<PojoToClientFlattened> observableList= FXCollections.observableArrayList();
                                PojoToClient ptc=gson.fromJson(task.getValue(),PojoToClient.class);
                                ArrayList<String> channelNames=ptc.getChannelName();
                                ArrayList<Integer> subcounts=ptc.getNumberOfSubscribers();
                                for(int i=0;i<channelNames.size();i++){
                                    PojoToClientFlattened ptcf=new PojoToClientFlattened();
                                    ptcf.setChannelName(channelNames.get(i));
                                    ptcf.setNumberOfSubscribers(subcounts.get(i));
                                    observableList.add(ptcf);
                                }
                                vcc.setPtfc(observableList);
                            });
                            Thread thread=new Thread(task);
                            thread.start();
                            content.getChildren().setAll(rt);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currentScreen = 1;
                        drawer.close();
                        drawer.toBack();
                    }
                }
            });

            watchHistoryBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (currentScreen != 2) {
                        headingTxt.setText("Watch History...");

                        try {
                            FXMLLoader loader=new FXMLLoader((getClass().getResource("ui/WatchHistory.fxml")));
                            Parent rt = loader.load();

                            WatchHistoryController whc=loader.getController();
                            whc.setContent(content);
                            PojoFromClient pfc=new PojoFromClient();
                            Gson gson = new GsonBuilder().serializeNulls().create();
                            pfc.setHeader(11);
                            pfc.setUserName(userName);
                            Task<String> task=new Task<String>() {
                                @Override
                                protected String call() throws Exception {
                                    output.println(gson.toJson(pfc));
                                    return input.readLine();
                                }
                            };
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui/progress.fxml"));
                            Parent root1 = (Parent) fxmlLoader.load();
                            progress = new Stage(StageStyle.UNDECORATED);
                            progress.initModality(Modality.WINDOW_MODAL);
                            progress.setScene(new Scene(root1));
                            progress.setAlwaysOnTop(true);
                            progress.show();
                            task.setOnSucceeded(workerStateEvent -> {
                                progress.close();
                                ObservableList<PojoToClientFlattened> observableList= FXCollections.observableArrayList();
                                PojoToClient ptc=gson.fromJson(task.getValue(),PojoToClient.class);
                                ArrayList<String> channelNames=ptc.getChannelName();
                                ArrayList<String> videoNames=ptc.getVideoName();
                                ArrayList<String> watchTimes=ptc.getWatchTime();
                                for(int i=0;i<channelNames.size();i++){
                                    PojoToClientFlattened ptcf=new PojoToClientFlattened();
                                    ptcf.setChannelName(channelNames.get(i));
                                    ptcf.setWatchTime(watchTimes.get(i));
                                    ptcf.setVideoName(videoNames.get(i));
                                    observableList.add(ptcf);
                                }
                                whc.setWatchHistoryList(observableList);
                            });
                            Thread thread=new Thread(task);
                            thread.start();
                            content.getChildren().setAll(rt);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currentScreen = 2;
                        drawer.close();
                        drawer.toBack();
                    }
                }
            });

            watchLaterBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (currentScreen != 3) {
                        headingTxt.setText("Watch Later...");

                        try {
                            FXMLLoader loader=new FXMLLoader((getClass().getResource("ui/WatchLater.fxml")));
                            Parent rt = loader.load();

                            WatchLaterController wlc=loader.getController();
                            wlc.setContent(content);
                            PojoFromClient pfc=new PojoFromClient();
                            Gson gson = new GsonBuilder().serializeNulls().create();
                            pfc.setHeader(22);
                            pfc.setUserName(userName);
                            Task<String> task=new Task<String>() {
                                @Override
                                protected String call() throws Exception {
                                    output.println(gson.toJson(pfc));
                                    return input.readLine();
                                }
                            };
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui/progress.fxml"));
                            Parent root1 = (Parent) fxmlLoader.load();
                            progress = new Stage(StageStyle.UNDECORATED);
                            progress.initModality(Modality.WINDOW_MODAL);
                            progress.setScene(new Scene(root1));
                            progress.setAlwaysOnTop(true);
                            progress.show();
                            task.setOnSucceeded(workerStateEvent -> {
                                progress.close();
                                ObservableList<PojoToClientFlattened> observableList= FXCollections.observableArrayList();
                                PojoToClient ptc=gson.fromJson(task.getValue(),PojoToClient.class);
                                ArrayList<String> channelNames=ptc.getChannelName();
                                ArrayList<String> videoNames=ptc.getVideoName();
                                ArrayList<String> watchLaterTimes=ptc.getWatchLaterTime();
                                for(int i=0;i<channelNames.size();i++){
                                    PojoToClientFlattened ptcf=new PojoToClientFlattened();
                                    ptcf.setChannelName(channelNames.get(i));
                                    ptcf.setWatchLaterTime(watchLaterTimes.get(i));
                                    ptcf.setVideoName(videoNames.get(i));
                                    observableList.add(ptcf);
                                }
                                wlc.setWatchLaterList(observableList);
                            });
                            Thread thread=new Thread(task);
                            thread.start();
                            content.getChildren().setAll(rt);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currentScreen = 3;
                        drawer.close();
                        drawer.toBack();
                    }
                }
            });

            accountBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (currentScreen != 4) {
                        headingTxt.setText("Account Summary...");

                        try {
                            FXMLLoader loader=new FXMLLoader((getClass().getResource("ui/AccountDetails.fxml")));
                            Parent rt = loader.load();

                            AccountDetailsController adc=loader.getController();

                            PojoFromClient pfc=new PojoFromClient();
                            Gson gson = new GsonBuilder().serializeNulls().create();
                            pfc.setHeader(5);
                            pfc.setUserName(userName);
                            Task<String> task=new Task<String>() {
                                @Override
                                protected String call() throws Exception {
                                    output.println(gson.toJson(pfc));
                                    return input.readLine();
                                }
                            };
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui/progress.fxml"));
                            Parent root1 = (Parent) fxmlLoader.load();
                            progress = new Stage(StageStyle.UNDECORATED);
                            progress.initModality(Modality.WINDOW_MODAL);
                            progress.setScene(new Scene(root1));
                            progress.setAlwaysOnTop(true);
                            progress.show();
                            task.setOnSucceeded(workerStateEvent -> {
                                progress.close();
                                PojoToClient ptc=gson.fromJson(task.getValue(),PojoToClient.class);
                                adc.setUserName(userName);
                                adc.setEmail(ptc.getEmailID());
                                adc.setCreationDate(ptc.getAccountCreationTime());
                            });
                            Thread thread=new Thread(task);
                            thread.start();
                            content.getChildren().setAll(rt);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currentScreen = 4;
                        drawer.close();
                        drawer.toBack();
                    }
                }
            });

            subChannelBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (currentScreen != 5) {
                        headingTxt.setText("Subscribed Channels...");

                        try {
                            FXMLLoader loader=new FXMLLoader((getClass().getResource("ui/ViewChannel.fxml")));
                            Parent rt = loader.load();

                            ViewChannelController vcc=loader.getController();
                            vcc.setTrendingBtn(trendingBtn);
                            vcc.setContent(content);
                            PojoFromClient pfc=new PojoFromClient();
                            Gson gson = new GsonBuilder().serializeNulls().create();
                            pfc.setHeader(13);
                            pfc.setUserName(userName);
                            Task<String> task=new Task<String>() {
                                @Override
                                protected String call() throws Exception {
                                    output.println(gson.toJson(pfc));
                                    return input.readLine();
                                }
                            };
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui/progress.fxml"));
                            Parent root1 = (Parent) fxmlLoader.load();
                            progress = new Stage(StageStyle.UNDECORATED);
                            progress.initModality(Modality.WINDOW_MODAL);
                            progress.setScene(new Scene(root1));
                            progress.setAlwaysOnTop(true);
                            progress.show();
                            task.setOnSucceeded(workerStateEvent -> {
                                progress.close();
                                ObservableList<PojoToClientFlattened> observableList= FXCollections.observableArrayList();
                                PojoToClient ptc=gson.fromJson(task.getValue(),PojoToClient.class);
                                ArrayList<String> channelNames=ptc.getChannelName();
                                ArrayList<String> subtime=ptc.getSubscribeTime();
                                for(int i=0;i<channelNames.size();i++){
                                    PojoToClientFlattened ptcf=new PojoToClientFlattened();
                                    ptcf.setChannelName(channelNames.get(i));
                                    ptcf.setSubscribeTime(subtime.get(i));
                                    observableList.add(ptcf);
                                }
                                vcc.setPtfc(observableList);
                                vcc.videoUploadable(false);
                            });
                            Thread thread=new Thread(task);
                            thread.start();
                            content.getChildren().setAll(rt);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currentScreen = 5;
                        drawer.close();
                        drawer.toBack();
                    }
                }
            });

            commentHistoryBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (currentScreen != 6) {
                        headingTxt.setText("Comment History...");

                        try {
                            FXMLLoader loader=new FXMLLoader((getClass().getResource("ui/CommentHistory.fxml")));
                            Parent rt = loader.load();

                            CommentHistoryController chc=loader.getController();
                            chc.setContent(content);
                            PojoFromClient pfc=new PojoFromClient();
                            Gson gson = new GsonBuilder().serializeNulls().create();
                            pfc.setHeader(12);
                            pfc.setUserName(userName);
                            Task<String> task=new Task<String>() {
                                @Override
                                protected String call() throws Exception {
                                    output.println(gson.toJson(pfc));
                                    return input.readLine();
                                }
                            };
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui/progress.fxml"));
                            Parent root1 = (Parent) fxmlLoader.load();
                            progress = new Stage(StageStyle.UNDECORATED);
                            progress.initModality(Modality.WINDOW_MODAL);
                            progress.setScene(new Scene(root1));
                            progress.setAlwaysOnTop(true);
                            progress.show();
                            task.setOnSucceeded(workerStateEvent -> {
                                progress.close();
                                ObservableList<PojoToClientFlattened> observableList= FXCollections.observableArrayList();
                                PojoToClient ptc=gson.fromJson(task.getValue(),PojoToClient.class);
                                ArrayList<String> channelNames=ptc.getChannelName();
                                ArrayList<String> videoNames=ptc.getVideoName();
                                ArrayList<String> creationTimes=ptc.getCommentCreationTime();
                                ArrayList<String> comments=ptc.getComment();
                                for(int i=0;i<channelNames.size();i++){
                                    PojoToClientFlattened ptcf=new PojoToClientFlattened();
                                    ptcf.setChannelName(channelNames.get(i));
                                    ptcf.setCommentCreationTime(creationTimes.get(i));
                                    ptcf.setComment(comments.get(i));
                                    ptcf.setVideoName(videoNames.get(i));
                                    observableList.add(ptcf);
                                }
                                chc.setCommentHistoryList(observableList);
                            });
                            Thread thread=new Thread(task);
                            thread.start();
                            content.getChildren().setAll(rt);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currentScreen = 6;
                        drawer.close();
                        drawer.toBack();
                    }
                }
            });


//            SideDrawerController controller = loader.getController();
//            controller.setCallback(this);
            drawer.setSidePane(box);
        } catch (IOException ex) {
            Logger.getLogger(MainHubController.class.getName()).log(Level.SEVERE, null, ex);
        }
        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
        transition.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();

            if (drawer.isOpened()) {
                drawer.close();
                drawer.toBack();
            } else {
                drawer.open();
                drawer.toFront();
                hamburger.toFront();
            }
        });
    }

    public void setTrending(String trending){
        this.trending=trending;
        trendingBtn.fire();
        System.out.println(trending);
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }


    public void setUserName(String userName) {
        this.userName = userName;
        helloTxt.setText("Hello, "+userName+" !");
    }

    public void setSock(Socket sock) {
        this.sock = sock;
        try {
            input = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            output = new PrintWriter(sock.getOutputStream(), true);
        }catch (IOException e){e.printStackTrace();}
    }

    public void onClose() throws Exception{
        PojoFromClient pfc=new PojoFromClient();
        Gson gson = new GsonBuilder().serializeNulls().create();
        System.out.println(userName);
        pfc.setHeader(21);
        pfc.setUserName(userName);
        Task<String> task=new Task<String>() {
            @Override
            protected String call() throws Exception {
                output.println(gson.toJson(pfc));
                return input.readLine();
            }
        };
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui/progress.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        progress = new Stage(StageStyle.UNDECORATED);
        progress.initModality(Modality.WINDOW_MODAL);
        progress.setScene(new Scene(root1));
        progress.setAlwaysOnTop(true);
        progress.show();
        task.setOnSucceeded(workerStateEvent -> {

            progress.close();
            System.out.println(task.getValue());
            Platform.exit();
        });
        Thread thread=new Thread(task);
        thread.start();
    }
    public static void loadPlayer(AnchorPane content,String videoName){
        BufferedReader input=MainHubController.input;
        PrintWriter output=MainHubController.output;
        String userName=MainHubController.userName;
        PojoFromClient pfc=new PojoFromClient();
        Gson gson = new GsonBuilder().serializeNulls().create();

        pfc.setVideoName(videoName);
        pfc.setUserName(userName);
        pfc.setHeader(4);
        Task<String> task=new Task<String>() {
            @Override
            protected String call() throws Exception {
                output.println(gson.toJson(pfc));
                return input.readLine();
            }
        };

        FXMLLoader loader1=new FXMLLoader(MainHubController.class.getResource("ui/PlayerHub.fxml"));
        try {


            Parent rt=loader1.load();
            PlayerHubController phc=loader1.getController();
            content.getChildren().setAll(rt);
            task.setOnSucceeded(workerStateEvent -> {
                phc.setInformation(task.getValue());
                MainHubController.currentScreen=-1;

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(task).start();
    }
}
