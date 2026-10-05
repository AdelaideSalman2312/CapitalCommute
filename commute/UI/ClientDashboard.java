package com.CapitalCommute.commute.UI;

import java.util.UUID;

import com.CapitalCommute.commute.UI.components.BookingTablePanel;
import com.CapitalCommute.commute.UI.components.NewBookingPanel;
import com.CapitalCommute.commute.UI.components.PaymentTablePanel;
import com.CapitalCommute.commute.UI.components.ProfilePanel;
import com.CapitalCommute.commute.util.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientDashboard extends BaseDashboard {

    public ClientDashboard(Stage primaryStage) {
        super(primaryStage);
    }

    @Override
    protected VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        sidebar.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-width: 0 1 0 0;"
        );

        Label menuLabel = new Label("CLIENT MENU");
        menuLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-padding: 10 0 10 0;"
        );

        Button dashboardBtn = createNavButton("🏠 Dashboard", "dashboard");
        Button bookingsBtn = createNavButton("🚗 My Bookings", "bookings");
        Button newBookingBtn = createNavButton("➕ New Booking", "new_booking");
        Button paymentsBtn = createNavButton("💳 Payments", "payments");
        Button profileBtn = createNavButton("👤 Profile", "profile");

        sidebar.getChildren().addAll(
            menuLabel,
            dashboardBtn,
            bookingsBtn,
            newBookingBtn,
            paymentsBtn,
            profileBtn
        );

        return sidebar;
    }

    @Override
    protected void loadDefaultContent() {
        currentSection = "dashboard";
        showDashboard();
    }

    @Override
    protected void handleNavigation(String section) {
        contentArea.getChildren().clear();

        switch (section) {
            case "dashboard":
                showDashboard();
                break;
            case "bookings":
                showBookings();
                break;
            case "new_booking":
                showNewBooking();
                break;
            case "payments":
                showPayments();
                break;
            case "profile":
                showProfile();
                break;
        }

        this.setLeft(createSidebar());
    }

    private void showDashboard() {
        Label titleLabel = new Label("Dashboard");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );

        Label welcomeLabel = new Label("Welcome back, Client!");
        welcomeLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );

        VBox statsCard1 = createStatCard("Total Bookings", "12", ACCENT);
        VBox statsCard2 = createStatCard("Pending Rides", "3", WARNING);
        VBox statsCard3 = createStatCard("Completed Rides", "9", SUCCESS);

        javafx.scene.layout.HBox statsBox = new javafx.scene.layout.HBox(20);
        statsBox.getChildren().addAll(statsCard1, statsCard2, statsCard3);

        contentArea.getChildren().addAll(titleLabel, welcomeLabel, statsBox);
    }

    private void showBookings() {
        BookingTablePanel bookingPanel = new BookingTablePanel(
        );
        contentArea.getChildren().add(bookingPanel);
    }

    private void showNewBooking() {
        NewBookingPanel newBookingPanel = new NewBookingPanel(
            UUID.fromString(SessionManager.getCurrentUserId())
        );
        contentArea.getChildren().add(newBookingPanel);
    }

    private void showPayments() {
        PaymentTablePanel paymentPanel = new PaymentTablePanel(
            UUID.fromString(SessionManager.getCurrentUserId())
        );
        contentArea.getChildren().add(paymentPanel);
    }

    private void showProfile() {
        ProfilePanel profilePanel = new ProfilePanel(
        (SessionManager.getCurrentNationalId())
        );
        contentArea.getChildren().add(profilePanel);
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        card.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 32px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + color + ";"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );

        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }
}