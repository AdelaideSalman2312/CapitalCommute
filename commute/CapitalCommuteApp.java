
package com.CapitalCommute.commute;

import com.CapitalCommute.commute.UI.LoginPanel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CapitalCommuteApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        LoginPanel loginPanel = new LoginPanel(primaryStage);
        Scene scene = new Scene(loginPanel, 900, 650);
        primaryStage.setTitle("Capital Commute");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}