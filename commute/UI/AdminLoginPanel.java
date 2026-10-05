package com.CapitalCommute.commute.UI;

import com.CapitalCommute.commute.DTO.LoginRequest;
import com.CapitalCommute.commute.DTO.LoginResponse;
import com.CapitalCommute.commute.client.ApiClient;
import com.CapitalCommute.commute.util.SessionManager;
import com.google.gson.Gson;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminLoginPanel extends VBox {
    private TextField emailField;
    private PasswordField passwordField;
    private Label messageLabel;
    private Stage primaryStage;
    private Gson gson;

    public AdminLoginPanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.gson = new Gson();
        initializeUI();
    }

    private void initializeUI() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: #0f172a;");

        Label titleLabel = new Label("ADMIN LOGIN");
        titleLabel.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #3b82f6;"
        );

        emailField = new TextField();
        emailField.setPromptText("Admin Email");
        emailField.setStyle(getFieldStyle());

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle(getFieldStyle());

        Button loginButton = new Button("Login as Admin");
        loginButton.setStyle(getButtonStyle());
        loginButton.setOnAction(e -> handleAdminLogin());

        Button backButton = new Button("Back to User Login");
        backButton.setStyle(getSecondaryButtonStyle());
        backButton.setOnAction(e -> showUserLogin());

        messageLabel = new Label();
        messageLabel.setWrapText(true);

        this.getChildren().addAll(titleLabel, emailField, passwordField, loginButton, backButton, messageLabel);
    }

    private void handleAdminLogin() {
        try {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showError("Please enter email and password");
                return;
            }

            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(email);
            loginRequest.setPassword(password);

            String requestJson = gson.toJson(loginRequest);
            String responseJson = ApiClient.post("/auth/login", requestJson);
            LoginResponse loginResponse = gson.fromJson(responseJson, LoginResponse.class);

            if (loginResponse != null && loginResponse.getRole() != null) {
                if ("ADMIN".equals(loginResponse.getRole())) {
                    SessionManager.setSession(
                        loginResponse.getRole(),
                        loginResponse.getUserId(),
                        loginResponse.getNationalId()
                    );
                    
                    AdminDashboard adminDashboard = new AdminDashboard(primaryStage);
                    Scene scene = new Scene(adminDashboard, 1400, 900);
                    primaryStage.setScene(scene);
                    primaryStage.setMaximized(true);
                } else {
                    showError("Access denied. Admin privileges required.");
                }
            } else {
                showError("Invalid admin credentials");
            }
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showUserLogin() {
        LoginPanel loginPanel = new LoginPanel(primaryStage);
        Scene scene = new Scene(loginPanel, 800, 600);
        primaryStage.setScene(scene);
    }

    private String getFieldStyle() {
        return "-fx-background-color: #334155;" +
               "-fx-border-color: #475569;" +
               "-fx-border-radius: 8;" +
               "-fx-background-radius: 8;" +
               "-fx-text-fill: #f1f5f9;" +
               "-fx-font-size: 14px;" +
               "-fx-padding: 12 16 12 16;" +
               "-fx-pref-width: 300px;";
    }

    private String getButtonStyle() {
        return "-fx-background-color: #3b82f6;" +
               "-fx-text-fill: white;" +
               "-fx-font-size: 14px;" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 8;" +
               "-fx-padding: 12 32 12 32;" +
               "-fx-cursor: hand;" +
               "-fx-pref-width: 300px;";
    }

    private String getSecondaryButtonStyle() {
        return "-fx-background-color: #334155;" +
               "-fx-text-fill: #f1f5f9;" +
               "-fx-font-size: 12px;" +
               "-fx-background-radius: 6;" +
               "-fx-padding: 8 16 8 16;" +
               "-fx-cursor: hand;";
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #ef4444;");
    }
}
