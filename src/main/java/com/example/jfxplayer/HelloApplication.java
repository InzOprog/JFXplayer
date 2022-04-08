package com.example.jfxplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private buttonController controller;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("playerGUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("KajPlayer - now via JFX!");
        stage.setScene(scene);
        stage.show();

        controller = fxmlLoader.getController();
        controller.initScene();
    }

    @Override
    public void stop()
    {
     controller.killThread();
    }

    public static void main(String[] args) {
        launch();
    }
}