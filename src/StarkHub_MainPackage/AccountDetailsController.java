package StarkHub_MainPackage;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountDetailsController implements Initializable {
    public Label emailTxt;
    public Label dateTxt;
    public Label unameTxt;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setUserName(String userName) {
        unameTxt.setText(userName);
    }

    public void setEmail(String emailID) {
        emailTxt.setText(emailID);
    }

    public void setCreationDate(String accountCreationTime) {
        dateTxt.setText(accountCreationTime);
    }
}
