package com.CapitalCommute.commute.UI;

import java.time.LocalDateTime;

import com.CapitalCommute.commute.UI.components.BookingTablePanel;
import com.CapitalCommute.commute.UI.components.PaymentTablePanel;
import com.CapitalCommute.commute.UI.components.UserTablePanel;
import com.CapitalCommute.commute.UI.components.VehicleTablePanel;
import com.CapitalCommute.commute.client.ApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminDashboard extends BaseDashboard {

    private Gson gson;

    public AdminDashboard(Stage primaryStage) {
        super(primaryStage);
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                (com.google.gson.JsonDeserializer<LocalDateTime>) (json, type, context) ->
                    LocalDateTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class,
                (com.google.gson.JsonSerializer<LocalDateTime>) (src, type, context) ->
                    new com.google.gson.JsonPrimitive(src.toString()))
            .create();
    }

    // ==================== SIDEBAR ====================
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

        Label menuLabel = new Label("ADMIN MENU");
        menuLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-padding: 10 0 10 0;"
        );

        Button dashboardBtn = createNavButton("🏠 Dashboard", "dashboard");
        Button usersBtn = createNavButton("👥 Manage Users", "users");
        Button driversBtn = createNavButton("🚗 Manage Drivers", "drivers");
        Button vehiclesBtn = createNavButton("🚙 Manage Vehicles", "vehicles");
        Button bookingsBtn = createNavButton("📋 All Bookings", "bookings");
        Button paymentsBtn = createNavButton("💳 All Payments", "payments");
        Button reportsBtn = createNavButton("📊 Reports", "reports");
        Button gpsTrackingBtn = createNavButton("📍 GPS Tracking", "gps");

        sidebar.getChildren().addAll(
            menuLabel,
            dashboardBtn,
            usersBtn,
            driversBtn,
            vehiclesBtn,
            bookingsBtn,
            paymentsBtn,
            reportsBtn,
            gpsTrackingBtn
        );

        return sidebar;
    }

    // ==================== LIFECYCLE METHODS ====================
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
            case "users":
                showUsers();
                break;
            case "drivers":
                showDrivers();
                break;
            case "vehicles":
                showVehicles();
                break;
            case "bookings":
                showBookings();
                break;
            case "payments":
                showPayments();
                break;
            case "reports":
                showReports();
                break;
            case "gps":
                showGPSTracking();
                break;
        }
        
        this.setLeft(createSidebar());
    }

    // ==================== DASHBOARD VIEWS ====================
    private void showDashboard() {
        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );

        Label welcomeLabel = new Label("System Overview");
        welcomeLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );

        VBox statsCard1 = createStatCard("Total Users", "1,234", ACCENT);
        VBox statsCard2 = createStatCard("Active Drivers", "89", SUCCESS);
        VBox statsCard3 = createStatCard("Today's Bookings", "47", WARNING);
        VBox statsCard4 = createStatCard("Revenue Today", "KES 85,000", SUCCESS);

        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(statsCard1, statsCard2, statsCard3, statsCard4);

        contentArea.getChildren().addAll(titleLabel, welcomeLabel, statsBox);
    }

    // ==================== USER MANAGEMENT ====================
    private void showUsers() {
        Label titleLabel = createSectionTitle("Manage Users");
        Label infoLabel = createSectionInfo("View and manage all system users");
        
        UserTablePanel userPanel = new UserTablePanel("all");
        
        VBox userContainer = createContentContainer(titleLabel, infoLabel, userPanel);
        contentArea.getChildren().add(userContainer);
    }

    private void showDrivers() {
        Label titleLabel = createSectionTitle("Manage Drivers");
        Label infoLabel = createSectionInfo("View and manage all drivers");
        
        UserTablePanel driverPanel = new UserTablePanel("driver");
        
        VBox driverContainer = createContentContainer(titleLabel, infoLabel, driverPanel);
        contentArea.getChildren().add(driverContainer);
    }

    // ==================== VEHICLE MANAGEMENT ====================
    private void showVehicles() {
        Label titleLabel = createSectionTitle("Manage Vehicles");
        Label infoLabel = createSectionInfo("View and manage all vehicles");
        
        VehicleTablePanel vehiclePanel = new VehicleTablePanel(true);
        
        VBox vehicleContainer = createContentContainer(titleLabel, infoLabel, vehiclePanel);
        contentArea.getChildren().add(vehicleContainer);
    }

    // ==================== BOOKINGS & PAYMENTS ====================
    private void showBookings() {
        Label titleLabel = createSectionTitle("All Bookings");
        Label infoLabel = createSectionInfo("View all system bookings");
        
        BookingTablePanel bookingPanel = new BookingTablePanel();
        
        VBox bookingContainer = createContentContainer(titleLabel, infoLabel, bookingPanel);
        contentArea.getChildren().add(bookingContainer);
    }

    private void showPayments() {
        Label titleLabel = createSectionTitle("All Payments");
        Label infoLabel = createSectionInfo("View all payment transactions");
        
        PaymentTablePanel paymentPanel = new PaymentTablePanel(null);
        
        VBox paymentContainer = createContentContainer(titleLabel, infoLabel, paymentPanel);
        contentArea.getChildren().add(paymentContainer);
    }

    // ==================== REPORTS & ANALYTICS ====================
    private void showReports() {
        Label titleLabel = createSectionTitle("System Reports");
        Label infoLabel = createSectionInfo("Key Performance Indicators and Analytics");
        
        TabPane reportTabs = createReportTabs();
        
        Button refreshBtn = createRefreshButton();
        refreshBtn.setOnAction(e -> refreshReports(reportTabs));
        
        VBox container = new VBox(15);
        container.setPadding(new Insets(20, 0, 0, 0));
        container.getChildren().addAll(titleLabel, infoLabel, reportTabs, refreshBtn);
        
        contentArea.getChildren().add(container);
        
        loadKPIData();
    }
    
    private TabPane createReportTabs() {
        TabPane reportTabs = new TabPane();
        reportTabs.setStyle("-fx-background-color: transparent;");
        
        Tab kpiTab = new Tab("KPI Dashboard");
        kpiTab.setClosable(false);
        kpiTab.setContent(createKPIDashboard());
        
        Tab revenueTab = new Tab("Revenue Analytics");
        revenueTab.setClosable(false);
        revenueTab.setContent(createRevenueAnalytics());
        
        Tab userTab = new Tab("User Analytics");
        userTab.setClosable(false);
        userTab.setContent(createUserAnalytics());
        
        reportTabs.getTabs().addAll(kpiTab, revenueTab, userTab);
        
        return reportTabs;
    }
    
    private VBox createKPIDashboard() {
        VBox kpiBox = new VBox(20);
        kpiBox.setPadding(new Insets(20));
        kpiBox.setAlignment(Pos.TOP_CENTER);
        
        GridPane kpiGrid = new GridPane();
        kpiGrid.setHgap(15);
        kpiGrid.setVgap(15);
        kpiGrid.setAlignment(Pos.CENTER);
        
        kpiBox.getChildren().add(kpiGrid);
        
        return kpiBox;
    }
    
    private VBox createRevenueAnalytics() {
        VBox revenueBox = new VBox(20);
        revenueBox.setPadding(new Insets(20));
        revenueBox.setAlignment(Pos.TOP_CENTER);
        
        Label dailyRevenue = createRevenueLabel("Daily Revenue: KES 0");
        Label weeklyRevenue = createRevenueLabel("Weekly Revenue: KES 0");
        Label monthlyRevenue = createRevenueLabel("Monthly Revenue: KES 0");
        Label yearlyRevenue = createRevenueLabel("Yearly Revenue: KES 0");
        
        Button refreshRevenueBtn = createRefreshButton();
        refreshRevenueBtn.setOnAction(e -> loadRevenueData(dailyRevenue, weeklyRevenue, monthlyRevenue, yearlyRevenue));
        
        revenueBox.getChildren().addAll(dailyRevenue, weeklyRevenue, monthlyRevenue, yearlyRevenue, refreshRevenueBtn);
        
        loadRevenueData(dailyRevenue, weeklyRevenue, monthlyRevenue, yearlyRevenue);
        
        return revenueBox;
    }
    
    private VBox createUserAnalytics() {
        VBox userBox = new VBox(20);
        userBox.setPadding(new Insets(20));
        userBox.setAlignment(Pos.TOP_CENTER);
        
        Label totalUsers = createAnalyticsLabel("Total Users: 0", ACCENT);
        Label newUsersToday = createAnalyticsLabel("New Users Today: 0", TEXT);
        Label activeUsers = createAnalyticsLabel("Active Users: 0", TEXT);
        Label usersByRole = createMutedLabel("Users by Role: Clients: 0 | Drivers: 0 | | Admins: 0");
        
        Button refreshUsersBtn = createRefreshButton();
        refreshUsersBtn.setOnAction(e -> loadUserAnalytics(totalUsers, newUsersToday, activeUsers, usersByRole));
        
        userBox.getChildren().addAll(totalUsers, newUsersToday, activeUsers, usersByRole, refreshUsersBtn);
        
        loadUserAnalytics(totalUsers, newUsersToday, activeUsers, usersByRole);
        
        return userBox;
    }
    
    // ==================== GPS TRACKING ====================
    private void showGPSTracking() {
        Label titleLabel = createSectionTitle("GPS Live Tracking");
        Label infoLabel = createSectionInfo("Real-time vehicle tracking and location monitoring");
        
        GPSSimulationPanel gpsPanel = new GPSSimulationPanel();
        
        VBox gpsContainer = createContentContainer(titleLabel, infoLabel, gpsPanel);
        contentArea.getChildren().add(gpsContainer);
    }

    // ==================== DATA LOADING METHODS ====================
    private void loadKPIData() {
        try {
            String response = ApiClient.get("/admin/dashboard/kpi");
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            
            GridPane kpiGrid = getKPIGridFromUI();
            if (kpiGrid == null) return;
            
            kpiGrid.getChildren().clear();
            
            int row = 0;
            int col = 0;
            
            VBox kpi1 = createKPIBox("Total Revenue", json.get("totalRevenue").getAsString(), SUCCESS);
            VBox kpi2 = createKPIBox("Total Bookings", json.get("totalBookings").getAsString(), ACCENT);
            VBox kpi3 = createKPIBox("Active Rides", json.get("activeRides").getAsString(), WARNING);
            VBox kpi4 = createKPIBox("Completion Rate", json.get("completionRate").getAsString() + "%", SUCCESS);
            VBox kpi5 = createKPIBox("Avg Rating", json.get("avgRating").getAsString() + "★", ACCENT);
            VBox kpi6 = createKPIBox("Total Vehicles", json.get("totalVehicles").getAsString(), TEXT);
            VBox kpi7 = createKPIBox("Pending Maintenance", json.get("pendingMaintenance").getAsString(), WARNING);
            VBox kpi8 = createKPIBox("Customer Satisfaction", json.get("customerSatisfaction").getAsString() + "%", SUCCESS);
            
            kpiGrid.add(kpi1, col, row); col++;
            kpiGrid.add(kpi2, col, row); col++;
            kpiGrid.add(kpi3, col, row); col++;
            kpiGrid.add(kpi4, col, row); row++; col = 0;
            kpiGrid.add(kpi5, col, row); col++;
            kpiGrid.add(kpi6, col, row); col++;
            kpiGrid.add(kpi7, col, row); col++;
            kpiGrid.add(kpi8, col, row);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private GridPane getKPIGridFromUI() {
        try {
            if (contentArea.getChildren().size() > 2) {
                VBox container = (VBox) contentArea.getChildren().get(2);
                if (container.getChildren().size() > 2) {
                    TabPane tabPane = (TabPane) container.getChildren().get(2);
                    Tab kpiTab = tabPane.getTabs().get(0);
                    VBox kpiBox = (VBox) kpiTab.getContent();
                    return (GridPane) kpiBox.getChildren().get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void loadRevenueData(Label daily, Label weekly, Label monthly, Label yearly) {
        try {
            String response = ApiClient.get("/admin/reports/revenue");
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            
            daily.setText("Daily Revenue: KES " + String.format("%,.2f", json.get("daily").getAsDouble()));
            weekly.setText("Weekly Revenue: KES " + String.format("%,.2f", json.get("weekly").getAsDouble()));
            monthly.setText("Monthly Revenue: KES " + String.format("%,.2f", json.get("monthly").getAsDouble()));
            yearly.setText("Yearly Revenue: KES " + String.format("%,.2f", json.get("yearly").getAsDouble()));
            
        } catch (Exception e) {
            daily.setText("Daily Revenue: KES 0.00");
            weekly.setText("Weekly Revenue: KES 0.00");
            monthly.setText("Monthly Revenue: KES 0.00");
            yearly.setText("Yearly Revenue: KES 0.00");
            e.printStackTrace();
        }
    }
    
    private void loadUserAnalytics(Label total, Label newToday, Label active, Label byRole) {
        try {
            String response = ApiClient.get("/admin/reports/users");
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            
            total.setText("Total Users: " + json.get("total").getAsInt());
            newToday.setText("New Users Today: " + json.get("newToday").getAsInt());
            active.setText("Active Users: " + json.get("active").getAsInt());
            
            JsonObject roles = json.getAsJsonObject("byRole");
            byRole.setText(String.format("Users by Role: Clients: %d | Drivers: %d | Admins: %d",
                roles.get("CLIENT").getAsInt(),
                roles.get("DRIVER").getAsInt(),
                roles.get("ADMIN").getAsInt()));
            
        } catch (Exception e) {
            total.setText("Total Users: 0");
            newToday.setText("New Users Today: 0");
            active.setText("Active Users: 0");
            byRole.setText("Users by Role: Clients: 0 | Drivers: 0 | Admins: 0");
            e.printStackTrace();
        }
    }
    
    private void refreshReports(TabPane tabPane) {
        loadKPIData();
        
        if (tabPane.getTabs().get(1).getContent() instanceof VBox) {
            VBox revenueBox = (VBox) tabPane.getTabs().get(1).getContent();
            if (revenueBox.getChildren().size() >= 4) {
                loadRevenueData(
                    (Label) revenueBox.getChildren().get(0),
                    (Label) revenueBox.getChildren().get(1),
                    (Label) revenueBox.getChildren().get(2),
                    (Label) revenueBox.getChildren().get(3)
                );
            }
        }
        
        if (tabPane.getTabs().get(2).getContent() instanceof VBox) {
            VBox userBox = (VBox) tabPane.getTabs().get(2).getContent();
            if (userBox.getChildren().size() >= 4) {
                loadUserAnalytics(
                    (Label) userBox.getChildren().get(0),
                    (Label) userBox.getChildren().get(1),
                    (Label) userBox.getChildren().get(2),
                    (Label) userBox.getChildren().get(3)
                );
            }
        }
    }

    // ==================== UI COMPONENT HELPERS ====================
    private Label createSectionTitle(String title) {
        Label label = new Label(title);
        label.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );
        return label;
    }
    
    private Label createSectionInfo(String info) {
        Label label = new Label(info);
        label.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        return label;
    }
    
    private VBox createContentContainer(Label title, Label info, javafx.scene.Node content) {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20, 0, 0, 0));
        container.getChildren().addAll(title, info, content);
        return container;
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(180);
        card.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + color + ";"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );

        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }
    
    private VBox createKPIBox(String title, String value, String color) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(180);
        box.setPrefHeight(120);
        box.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;"
        );
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + color + ";"
        );
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );
        
        box.getChildren().addAll(valueLabel, titleLabel);
        return box;
    }
    
    private Label createRevenueLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: " + TEXT + ";");
        return label;
    }
    
    private Label createAnalyticsLabel(String text, String color) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: " + color + ";");
        return label;
    }
    
    private Label createMutedLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_MUTED + ";");
        return label;
    }
    
    private Button createRefreshButton() {
        Button button = new Button("Refresh Data");
        button.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16 8 16;" +
            "-fx-cursor: hand;"
        );
        return button;
    }
}