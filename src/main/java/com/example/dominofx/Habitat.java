package com.example.dominofx;

import com.example.dominofx.Controllers.HabitatController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Habitat extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Habitat.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 606, 400);
        stage.setTitle("domino");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(e->{
            System.exit(0);
        });
       HabitatController habitatController = fxmlLoader.getController();
        habitatController.getAllMusic();
        habitatController.startMusic();

    }
    public static void main(String[] args) {
        launch();
    }
}