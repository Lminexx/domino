package com.example.dominofx.Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class winWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button okButton;
    @FXML
    private Label resultLabel;

    @FXML
    void initialize() {

    }
    public void setResultText(String text) {
        resultLabel.setText(text);
    }
    @FXML
    void onOkButtonClicked(ActionEvent event) {
        Stage stage = (Stage) resultLabel.getScene().getWindow();
        stage.close();
    }


}
