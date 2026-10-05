package com.CapitalCommute.commute.UI;

import java.time.LocalDate;

import com.CapitalCommute.commute.DTO.LoginRequest;
import com.CapitalCommute.commute.DTO.LoginResponse;
import com.CapitalCommute.commute.client.ApiClient;
import com.CapitalCommute.commute.util.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPanel extends VBox {

    private TextField emailField;
    private PasswordField passwordField;
    private Label messageLabel;
    private Gson gson;
    private Stage primaryStage;

    private static final String BG_DARK      = "#0f172a";
    private static final String BG_SURFACE   = "#1e293b";
    private static final String BG_SURFACE2  = "#334155";
    private static final String ACCENT       = "#3b82f6";
    private static final String ACCENT_HOVER = "#2563eb";
    private static final String TEXT         = "#f1f5f9";
    private static final String TEXT_MUTED   = "#94a3b8";
    private static final String BORDER       = "#475569";

    private static final String FIELD_STYLE =
        "-fx-background-color: " + BG_SURFACE2 + ";" +
        "-fx-border-color: " + BORDER + ";" +
        "-fx-border-radius: 8;" +
        "-fx-background-radius: 8;" +
        "-fx-text-fill: " + TEXT + ";" +
        "-fx-font-size: 13px;" +
        "-fx-padding: 10 14 10 14;" +
        "-fx-pref-width: 300px;" +
        "-fx-prompt-text-fill: #64748b;";

    private static final String BTN_PRIMARY =
        "-fx-background-color: " + ACCENT + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 14px;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 8;" +
        "-fx-padding: 10 24 10 24;" +
        "-fx-cursor: hand;" +
        "-fx-pref-width: 300px;";

    private static final String BTN_PRIMARY_HOVER =
        "-fx-background-color: " + ACCENT_HOVER + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 14px;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 8;" +
        "-fx-padding: 10 24 10 24;" +
        "-fx-cursor: hand;" +
        "-fx-pref-width: 300px;";

    private static final String LABEL_STYLE =
        "-fx-font-size: 13px;" +
        "-fx-text-fill: " + TEXT_MUTED + ";";

    public LoginPanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, 
                (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> 
                    new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class, 
                (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> 
                    LocalDate.parse(json.getAsString()))
            .create();
        initializeUI();
    }

    private void initializeUI() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: " + BG_DARK + ";");

        Label titleLabel = new Label("CAPITAL COMMUTE");
        titleLabel.setStyle(
            "-fx-font-size: 32px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-letter-spacing: 2px;"
        );

        Label subtitleLabel = new Label("Your trusted ride partner");
        subtitleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );

        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(32));
        card.setMaxWidth(380);
        card.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BG_SURFACE2 + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 8);"
        );

        Label emailLabel = new Label("Email Address");
        emailLabel.setStyle(LABEL_STYLE); 
        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setStyle(FIELD_STYLE); 

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle(LABEL_STYLE);
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle(FIELD_STYLE);

        Button loginButton = new Button("Sign In");
        loginButton.setStyle(BTN_PRIMARY);
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(BTN_PRIMARY_HOVER));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(BTN_PRIMARY));
        loginButton.setOnAction(e -> handleLogin());

        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        messageLabel.setAlignment(Pos.CENTER);

        Hyperlink registerLink = new Hyperlink("Don't have an account? Register here");
        registerLink.setStyle(
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-font-size: 13px;" +
            "-fx-border-color: transparent;"
        );
        registerLink.setOnAction(e -> showRegisterPanel());

        card.getChildren().addAll(
            emailLabel,
            emailField,
            passwordLabel,
            passwordField,
            loginButton,
            messageLabel,
            registerLink
        );

        this.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            new Label(""),
            card
        );

        passwordField.setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
    String email = emailField.getText().trim();
    String password = passwordField.getText();

    if (email.isEmpty() || password.isEmpty()) {
        showError("Please enter both email and password");
        return;
    }

    try {
        LoginRequest loginRequest = new LoginRequest(email, password);
        String requestJson = gson.toJson(loginRequest);
        System.out.println("Sending login request: " + requestJson);
        
        String responseJson = ApiClient.post("/auth/login", requestJson);
        System.out.println("Raw response: " + responseJson);
        
        LoginResponse loginResponse = gson.fromJson(responseJson, LoginResponse.class);
        
        System.out.println("Parsed response - Role: " + loginResponse.getRole());
        System.out.println("Parsed response - Message: " + loginResponse.getMessage());
        System.out.println("Parsed response - UserId: " + loginResponse.getUserId());
        System.out.println("Parsed response - NationalId: " + loginResponse.getNationalId());

        if (loginResponse.getRole() != null) {
            String normalizedRole = loginResponse.getRole().toUpperCase();
            SessionManager.setSession(
                normalizedRole,
                loginResponse.getUserId(),
                loginResponse.getNationalId()
            );
            SessionManager.setCurrentEmail(email);
            navigateToDashboard(normalizedRole);
        } else {
            showError(loginResponse.getMessage() != null ? loginResponse.getMessage() : "Login failed");
        }

    } catch (Exception e) {
        showError("Login failed: " + e.getMessage());
        e.printStackTrace();
    }
}

    private void navigateToDashboard(String role) {
        Scene scene;
        switch (role) {
            case "CLIENT":
                scene = new Scene(new ClientDashboard(primaryStage), 1500, 800);
                break;
            case "DRIVER":
                scene = new Scene(new DriverDashboard(primaryStage), 1500, 800);
                break;
            case "ADMIN":
                scene = new Scene(new AdminDashboard(primaryStage), 1500, 800);
                break;
            default:
                showError("Unknown role: " + role);
                return;
        }
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
    }

    private void showRegisterPanel() {
        Scene scene = new Scene(new RegistrationPanel(primaryStage), 900, 700);
        primaryStage.setScene(scene);
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle(
            "-fx-text-fill: #ef4444;" +
            "-fx-font-size: 12px;"
        );
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle(
            "-fx-text-fill: #22c55e;" +
            "-fx-font-size: 12px;"
        );
    }
}