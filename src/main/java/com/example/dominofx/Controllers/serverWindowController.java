package com.example.dominofx.Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

public class serverWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button copyIP;

    @FXML
    private Label ipText;

    @FXML
    private Button onButton;

    @FXML
    void onClickButton(ActionEvent event) {
        Stage stage = (Stage) ipText.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onClickCopyIp(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(ipText.getText());
        clipboard.setContent(content);
    }

    public void setIpText(String ip){
        ipText.setText(ip);
    }

    @FXML
    void initialize() {

    }


}
