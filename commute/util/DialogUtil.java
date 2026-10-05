package com.CapitalCommute.commute.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

public class DialogUtil {
    
    private static final String BG_DARK = "#0f172a";
    private static final String BG_SURFACE = "#1e293b";
    private static final String ACCENT = "#3b82f6";
    private static final String SUCCESS = "#22c55e";
    private static final String DANGER = "#ef4444";
    private static final String WARNING = "#f59e0b";
    private static final String TEXT = "#f1f5f9";
    
   
    public static boolean showSuccess(String title, String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        styleAlert(alert, SUCCESS);
        
        alert.showAndWait();
        return alert.getResult() == ButtonType.OK;
    }
    
    /**
     * Show error dialog
     */
    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        styleAlert(alert, DANGER);
        alert.showAndWait();
    }
    
    /**
     * Show warning dialog
     */
    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        styleAlert(alert, WARNING);
        alert.showAndWait();
    }
    
    
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        styleAlert(alert, ACCENT);
        
        alert.showAndWait();
        return alert.getResult() == ButtonType.OK;
    }
    
    
    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        styleAlert(alert, ACCENT);
        alert.showAndWait();
    }
    
    
    private static void styleAlert(Alert alert, String accentColor) {
        alert.initStyle(StageStyle.DECORATED);
        
        
        alert.getDialogPane().setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-border-color: " + accentColor + ";" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 10px;" +
            "-fx-background-radius: 10px;" +
            "-fx-padding: 20px;"
        );
        
        // Header area
        try {
            alert.getDialogPane().lookup(".header-panel").setStyle(
                "-fx-background-color: " + BG_DARK + ";" +
                "-fx-background-radius: 8px 8px 0 0;" +
                "-fx-padding: 15px;"
            );
        } catch (Exception e) {
            // Header panel might not exist
        }
        
        // Header text
        try {
            alert.getDialogPane().lookup(".header-panel .label").setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
            );
        } catch (Exception e) {
            // Ignore if not found
        }
        
        // Content text
        try {
            alert.getDialogPane().lookup(".content.label").setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 10px 0 10px 0;"
            );
        } catch (Exception e) {
            // Ignore if not found
        }
        
        // Buttons
        alert.getDialogPane().lookupAll(".button").forEach(node -> {
            node.setStyle(
                "-fx-background-color: " + accentColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6px;" +
                "-fx-padding: 8 16 8 16;" +
                "-fx-cursor: hand;"
            );
            
            // Hover effect
            node.setOnMouseEntered(e -> {
                node.setStyle(
                    "-fx-background-color: derive(" + accentColor + ", -10%);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 6px;" +
                    "-fx-padding: 8 16 8 16;" +
                    "-fx-cursor: hand;"
                );
            });
            
            node.setOnMouseExited(e -> {
                node.setStyle(
                    "-fx-background-color: " + accentColor + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 6px;" +
                    "-fx-padding: 8 16 8 16;" +
                    "-fx-cursor: hand;"
                );
            });
        });
    }
}