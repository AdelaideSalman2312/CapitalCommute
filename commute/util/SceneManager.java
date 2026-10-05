package com.CapitalCommute.commute.util;
import javafx.application.Platform;
import javafx.scene.Scene;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class SceneManager {
    
    
    public static void applyEscapeListener(Scene scene, Stage primaryStage) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                handleExit(primaryStage);
            }
        });
    }
    
    
    private static void handleExit(Stage primaryStage) {
        boolean confirmed = DialogUtil.showConfirmation(
            "Exit Application",
            "Are you sure you want to exit?",
            "Any unsaved changes will be lost."
        );
        
        if (confirmed) {
            Platform.exit();
            System.exit(0);
        }
    }
    
   
    public static void switchScene(Stage primaryStage, Scene newScene) {
        applyEscapeListener(newScene, primaryStage);
        primaryStage.setScene(newScene);
    }
}

