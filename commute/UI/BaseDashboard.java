package com.CapitalCommute.commute.UI;

import com.CapitalCommute.commute.util.DialogUtil;
import com.CapitalCommute.commute.util.SceneManager;
import com.CapitalCommute.commute.util.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class BaseDashboard extends BorderPane {

    // ── Theme Colors ───────────────────────────────────────────────
    protected static final String BG_DARK = "#0f172a";
    protected static final String BG_SURFACE = "#1e293b";
    protected static final String BG_SURFACE2 = "#334155";
    protected static final String ACCENT = "#3b82f6";
    protected static final String ACCENT_HOVER = "#2563eb";
    protected static final String SUCCESS = "#22c55e";
    protected static final String WARNING = "#f59e0b";
    protected static final String DANGER = "#ef4444";
    protected static final String TEXT = "#f1f5f9";
    protected static final String TEXT_MUTED = "#94a3b8";
    protected static final String BORDER = "#475569";

    // ── Button Styles ───────────────────────────────────────────────
    protected static final String BTN_NAV =
        "-fx-background-color: transparent;" +
        "-fx-text-fill: " + TEXT_MUTED + ";" +
        "-fx-font-size: 14px;" +
        "-fx-padding: 12 20 12 20;" +
        "-fx-cursor: hand;" +
        "-fx-background-radius: 8;" +
        "-fx-border-width: 0;";

    protected static final String BTN_NAV_ACTIVE =
        "-fx-background-color: " + ACCENT + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 14px;" +
        "-fx-font-weight: bold;" +
        "-fx-padding: 12 20 12 20;" +
        "-fx-cursor: hand;" +
        "-fx-background-radius: 8;";

    protected static final String BTN_LOGOUT =
        "-fx-background-color: " + DANGER + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 13px;" +
        "-fx-font-weight: bold;" +
        "-fx-padding: 8 16 8 16;" +
        "-fx-cursor: hand;" +
        "-fx-background-radius: 6;";

    // ── Core Fields ───────────────────────────────────────────────
    protected Stage primaryStage;
    protected VBox contentArea;
    protected String currentSection = "";

    // ── Constructor ───────────────────────────────────────────────
    public BaseDashboard(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeUI();
    }

    // ── UI Setup ───────────────────────────────────────────────
    private void initializeUI() {
        this.setStyle("-fx-background-color: " + BG_DARK + ";");

        // Top Bar
        HBox topBar = createTopBar();
        this.setTop(topBar);

        // Left Sidebar
        VBox sidebar = createSidebar();
        this.setLeft(sidebar);

        // Content Area (wrapped in ScrollPane)
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(30));
        contentArea.setStyle("-fx-background-color: " + BG_DARK + ";");

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle(
            "-fx-background: " + BG_DARK + ";" +
            "-fx-background-color: " + BG_DARK + ";" +
            "-fx-border-color: transparent;"
        );
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        this.setCenter(scrollPane);

        // Load default content
        loadDefaultContent();
    }

    // ── Top Bar ───────────────────────────────────────────────
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-width: 0 0 1 0;"
        );

        Label titleLabel = new Label("CAPITAL COMMUTE");
        titleLabel.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-letter-spacing: 1.5px;"
        );

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        String email = SessionManager.getCurrentEmail();
        String role = SessionManager.getCurrentRole();
        Label userLabel = new Label(email + " (" + role + ")");
        userLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(BTN_LOGOUT);
        logoutButton.setOnAction(e -> handleLogout());

        topBar.getChildren().addAll(titleLabel, spacer, userLabel, logoutButton);
        return topBar;
    }

    // ── Abstract Methods ───────────────────────────────────────────────
    protected abstract VBox createSidebar();
    protected abstract void loadDefaultContent();
    protected abstract void handleNavigation(String section);

    // ── Navigation Button Factory ───────────────────────────────────────────────
    protected Button createNavButton(String text, String section) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);

        if (section.equals(currentSection)) {
            button.setStyle(BTN_NAV_ACTIVE);
        } else {
            button.setStyle(BTN_NAV);
        }

        button.setOnMouseEntered(e -> {
            if (!section.equals(currentSection)) {
                button.setStyle(BTN_NAV + "-fx-background-color: " + BG_SURFACE2 + ";");
            }
        });

        button.setOnMouseExited(e -> {
            if (!section.equals(currentSection)) {
                button.setStyle(BTN_NAV);
            }
        });

        button.setOnAction(e -> {
            currentSection = section;
            handleNavigation(section);
        });

        return button;
    }

    // ── Logout Handling ───────────────────────────────────────────────
    private void handleLogout() {
        boolean confirmed = DialogUtil.showConfirmation(
            "Logout",
            "Are you sure you want to logout?",
            "You will be redirected to the login screen."
        );

        if (confirmed) {
            SessionManager.clearSession();
            Scene scene = new Scene(new LoginPanel(primaryStage), 1000, 600);
            SceneManager.switchScene(primaryStage, scene);
        }
    }
}