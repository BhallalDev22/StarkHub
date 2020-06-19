package StarkHub_MainPackage;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginSignupController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private MediaView mediaView;
    private MediaPlayer mp;
    private Media me;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            String path = new File("src/Graphics/LoginSignupBackground.mp4").getAbsolutePath();
            System.out.println(path);
            me = new Media(new File(path).toURI().toString());
            mp = new MediaPlayer(me);
            mediaView.setMediaPlayer(mp);
            mp.setVolume(0);
            mp.setAutoPlay(true);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
