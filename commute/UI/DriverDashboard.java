package com.CapitalCommute.commute.UI;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.CapitalCommute.commute.UI.components.ProfilePanel;
import com.CapitalCommute.commute.client.ApiClient;
import com.CapitalCommute.commute.model.Booking;
import com.CapitalCommute.commute.model.Payment;
import com.CapitalCommute.commute.model.Vehicle;
import com.CapitalCommute.commute.model.enums.BookingStatus;
import com.CapitalCommute.commute.util.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DriverDashboard extends BaseDashboard {

    private UUID currentDriverId;
    private Gson gson;
    private TableView<Booking> ridesTable;
    private TableView<Booking> availableRidesTable;
    private TableView<Payment> earningsTable;
    private Label vehicleInfoLabel;

    public DriverDashboard(Stage primaryStage) {
        super(primaryStage);
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                    new com.google.gson.JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>) (json, type, context) ->
                    LocalDateTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class,
                (JsonSerializer<LocalDateTime>) (src, type, context) ->
                    new com.google.gson.JsonPrimitive(src.toString()))
            .create();
        
        String driverId = SessionManager.getCurrentUserId();
        if (driverId != null && !driverId.isEmpty()) {
            this.currentDriverId = UUID.fromString(driverId);
        }
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

        Label menuLabel = new Label("DRIVER MENU");
        menuLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-padding: 10 0 10 0;"
        );

        Button dashboardBtn = createNavButton("🏠 Dashboard", "dashboard");
        Button ridesBtn = createNavButton("🚗 My Rides", "rides");
        Button availableBtn = createNavButton("📋 Available Rides", "available");
        Button vehicleBtn = createNavButton("🚙 My Vehicle", "vehicle");
        Button earningsBtn = createNavButton("💰 Earnings", "earnings");
        Button profileBtn = createNavButton("👤 Profile", "profile");

        sidebar.getChildren().addAll(
            menuLabel,
            dashboardBtn,
            ridesBtn,
            availableBtn,
            vehicleBtn,
            earningsBtn,
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
            case "rides":
                showRides();
                break;
            case "available":
                showAvailableRides();
                break;
            case "vehicle":
                showVehicle();
                break;
            case "earnings":
                showEarnings();
                break;
            case "profile":
                showProfile();
                break;
        }
        
        this.setLeft(createSidebar());
    }

    private void showDashboard() {
        Label titleLabel = new Label("Driver Dashboard");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );

        Label welcomeLabel = new Label("Welcome back, Driver!");
        welcomeLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );

        VBox statsCard1 = createStatCard("Total Rides", "0", ACCENT);
        VBox statsCard2 = createStatCard("Today's Rides", "0", SUCCESS);
        VBox statsCard3 = createStatCard("Rating", "0★", WARNING);
        VBox statsCard4 = createStatCard("Earnings Today", "KES 0", SUCCESS);

        javafx.scene.layout.HBox statsBox = new javafx.scene.layout.HBox(20);
        statsBox.getChildren().addAll(statsCard1, statsCard2, statsCard3, statsCard4);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16 8 16;" +
            "-fx-cursor: hand;"
        );
        refreshBtn.setOnAction(e -> refreshStats(statsCard1, statsCard2, statsCard4));

        contentArea.getChildren().addAll(titleLabel, welcomeLabel, statsBox, refreshBtn);
        loadStats(statsCard1, statsCard2, statsCard4);
    }

    private void showRides() {
        Label titleLabel = new Label("My Rides");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );

        Label infoLabel = new Label("View your ride history");
        infoLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");

        createRidesTable();
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16 8 16;" +
            "-fx-cursor: hand;"
        );
        refreshBtn.setOnAction(e -> loadDriverRides());

        VBox container = new VBox(15);
        container.getChildren().addAll(titleLabel, infoLabel, ridesTable, refreshBtn);

        contentArea.getChildren().add(container);
        loadDriverRides();
    }

    private void showAvailableRides() {
        Label titleLabel = new Label("Available Rides");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );

        Label infoLabel = new Label("Accept ride requests from clients");
        infoLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");

        createAvailableRidesTable();
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16 8 16;" +
            "-fx-cursor: hand;"
        );
        refreshBtn.setOnAction(e -> loadAvailableRides());

        VBox container = new VBox(15);
        container.getChildren().addAll(titleLabel, infoLabel, availableRidesTable, refreshBtn);

        contentArea.getChildren().add(container);
        loadAvailableRides();
    }

    private void showVehicle() {
        Label titleLabel = new Label("My Vehicle");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );

        Label infoLabel = new Label("Manage your vehicle information");
        infoLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");

        vehicleInfoLabel = new Label("Loading vehicle information...");
        vehicleInfoLabel.setStyle("-fx-text-fill: " + TEXT + ";");
        vehicleInfoLabel.setWrapText(true);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16 8 16;" +
            "-fx-cursor: hand;"
        );
        refreshBtn.setOnAction(e -> loadVehicleInfo());

        VBox container = new VBox(15);
        container.getChildren().addAll(titleLabel, infoLabel, vehicleInfoLabel, refreshBtn);

        contentArea.getChildren().add(container);
        loadVehicleInfo();
    }

    private void showEarnings() {
        Label titleLabel = new Label("Earnings");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );

        Label infoLabel = new Label("Track your earnings and payouts");
        infoLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");

        createEarningsTable();
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16 8 16;" +
            "-fx-cursor: hand;"
        );
        refreshBtn.setOnAction(e -> loadDriverEarnings());

        VBox container = new VBox(15);
        container.getChildren().addAll(titleLabel, infoLabel, earningsTable, refreshBtn);

        contentArea.getChildren().add(container);
        loadDriverEarnings();
    }

    private void showProfile() {
        ProfilePanel profilePanel = new ProfilePanel(
            SessionManager.getCurrentNationalId()
        );
        contentArea.getChildren().add(profilePanel);
    }

    private void createRidesTable() {
        ridesTable = new TableView<>();
        ridesTable.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;"
        );

        TableColumn<Booking, String> idCol = new TableColumn<>("Ride ID");
        idCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBookingId().toString().substring(0, 8)));
        idCol.setPrefWidth(100);

        TableColumn<Booking, String> pickupCol = new TableColumn<>("Pickup");
        pickupCol.setCellValueFactory(new PropertyValueFactory<>("pickupLocation"));
        pickupCol.setPrefWidth(150);

        TableColumn<Booking, String> dropoffCol = new TableColumn<>("Dropoff");
        dropoffCol.setCellValueFactory(new PropertyValueFactory<>("dropoffLocation"));
        dropoffCol.setPrefWidth(150);

        TableColumn<Booking, Double> fareCol = new TableColumn<>("Fare");
        fareCol.setCellValueFactory(new PropertyValueFactory<>("fareAmount"));
        fareCol.setPrefWidth(100);
        fareCol.setCellFactory(tc -> new javafx.scene.control.TableCell<Booking, Double>() {
            @Override
            protected void updateItem(Double fare, boolean empty) {
                super.updateItem(fare, empty);
                if (empty || fare == null) {
                    setText(null);
                } else {
                    setText("KES " + String.format("%,.2f", fare));
                }
            }
        });

        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBookingStatus().toString()));
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(tc -> new javafx.scene.control.TableCell<Booking, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                } else {
                    setText(status);
                    if (status.equals("DRIVER_ASSIGNED")) {
                        setStyle("-fx-text-fill: " + ACCENT + "; -fx-font-weight: bold;");
                    } else if (status.equals("TRIP_COMPLETED")) {
                        setStyle("-fx-text-fill: " + SUCCESS + "; -fx-font-weight: bold;");
                    }
                }
            }
        });

        TableColumn<Booking, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getBookingTime();
            if (date != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return new javafx.beans.property.SimpleStringProperty(date.format(formatter));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        dateCol.setPrefWidth(150);

        ridesTable.getColumns().addAll(idCol, pickupCol, dropoffCol, fareCol, statusCol, dateCol);
        ridesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void createAvailableRidesTable() {
        availableRidesTable = new TableView<>();
        availableRidesTable.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;"
        );

        TableColumn<Booking, String> idCol = new TableColumn<>("Ride ID");
        idCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBookingId().toString().substring(0, 8)));
        idCol.setPrefWidth(100);

        TableColumn<Booking, String> pickupCol = new TableColumn<>("Pickup");
        pickupCol.setCellValueFactory(new PropertyValueFactory<>("pickupLocation"));
        pickupCol.setPrefWidth(150);

        TableColumn<Booking, String> dropoffCol = new TableColumn<>("Dropoff");
        dropoffCol.setCellValueFactory(new PropertyValueFactory<>("dropoffLocation"));
        dropoffCol.setPrefWidth(150);

        TableColumn<Booking, Double> fareCol = new TableColumn<>("Fare");
        fareCol.setCellValueFactory(new PropertyValueFactory<>("fareAmount"));
        fareCol.setPrefWidth(100);
        fareCol.setCellFactory(tc -> new javafx.scene.control.TableCell<Booking, Double>() {
            @Override
            protected void updateItem(Double fare, boolean empty) {
                super.updateItem(fare, empty);
                if (empty || fare == null) {
                    setText(null);
                } else {
                    setText("KES " + String.format("%,.2f", fare));
                }
            }
        });

        TableColumn<Booking, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(100);
        actionCol.setCellFactory(tc -> new javafx.scene.control.TableCell<Booking, Void>() {
            private final Button acceptBtn = new Button("Accept");
            {
                acceptBtn.setStyle(
                    "-fx-background-color: " + SUCCESS + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 4;" +
                    "-fx-padding: 6 12 6 12;" +
                    "-fx-cursor: hand;"
                );
                acceptBtn.setOnAction(event -> {
                    Booking ride = getTableView().getItems().get(getIndex());
                    acceptRide(ride);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(acceptBtn);
                }
            }
        });

        availableRidesTable.getColumns().addAll(idCol, pickupCol, dropoffCol, fareCol, actionCol);
        availableRidesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void createEarningsTable() {
        earningsTable = new TableView<>();
        earningsTable.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;"
        );

        TableColumn<Payment, String> idCol = new TableColumn<>("Payment ID");
        idCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPaymentId().toString().substring(0, 8)));
        idCol.setPrefWidth(100);

        TableColumn<Payment, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("fareAmount"));
        amountCol.setPrefWidth(100);
        amountCol.setCellFactory(tc -> new javafx.scene.control.TableCell<Payment, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText("KES " + String.format("%,.2f", amount));
                }
            }
        });

        TableColumn<Payment, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        methodCol.setPrefWidth(100);

        TableColumn<Payment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(tc -> new javafx.scene.control.TableCell<Payment, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                } else {
                    setText(status);
                    if (status.equals("COMPLETED")) {
                        setStyle("-fx-text-fill: " + SUCCESS + "; -fx-font-weight: bold;");
                    }
                }
            }
        });

        TableColumn<Payment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getPaymentTime();
            if (date != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return new javafx.beans.property.SimpleStringProperty(date.format(formatter));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        dateCol.setPrefWidth(150);

        earningsTable.getColumns().addAll(idCol, amountCol, methodCol, statusCol, dateCol);
        earningsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadDriverRides() {
        try {
            String response = ApiClient.get("/rides/driver/" + currentDriverId);
            java.lang.reflect.Type listType = new TypeToken<List<Booking>>(){}.getType();
            List<Booking> rides = gson.fromJson(response, listType);
            ridesTable.getItems().setAll(rides);
        } catch (Exception e) {
            System.err.println("Failed to load rides: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAvailableRides() {
        try {
            String response = ApiClient.get("/rides/available");
            java.lang.reflect.Type listType = new TypeToken<List<Booking>>(){}.getType();
            List<Booking> rides = gson.fromJson(response, listType);
            availableRidesTable.getItems().setAll(rides);
        } catch (Exception e) {
            System.err.println("Failed to load available rides: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDriverEarnings() {
        try {
            String response = ApiClient.get("/payments/driver/" + currentDriverId);
            java.lang.reflect.Type listType = new TypeToken<List<Payment>>(){}.getType();
            List<Payment> payments = gson.fromJson(response, listType);
            earningsTable.getItems().setAll(payments);
        } catch (Exception e) {
            System.err.println("Failed to load earnings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadVehicleInfo() {
        try {
            String response = ApiClient.get("/vehicles/driver/" + currentDriverId);
            Vehicle vehicle = gson.fromJson(response, Vehicle.class);
            
            if (vehicle != null) {
                vehicleInfoLabel.setText(
                    "License Plate: " + vehicle.getLicensePlate() + "\n" +
                    "Make: " + vehicle.getMake() + "\n" +
                    "Model: " + vehicle.getModel() + "\n" +
                    "Year: " + vehicle.getYear() + "\n" +
                    "Status: " + vehicle.getVehicleStatus()
                );
            } else {
                vehicleInfoLabel.setText("No vehicle assigned yet.");
            }
        } catch (Exception e) {
            vehicleInfoLabel.setText("No vehicle assigned yet.");
            System.err.println("Failed to load vehicle info: " + e.getMessage());
        }
    }
    
    private void loadStats(VBox totalRidesCard, VBox todayRidesCard, VBox earningsCard) {
        try {
            String response = ApiClient.get("/stats/driver/" + currentDriverId);
            com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(response).getAsJsonObject();
            
            Label totalRidesLabel = (Label) totalRidesCard.getChildren().get(0);
            totalRidesLabel.setText(json.get("totalRides").getAsString());
            
            Label todayRidesLabel = (Label) todayRidesCard.getChildren().get(0);
            todayRidesLabel.setText(json.get("todayRides").getAsString());
            
            Label earningsLabel = (Label) earningsCard.getChildren().get(0);
            earningsLabel.setText("KES " + json.get("todayEarnings").getAsString());
            
        } catch (Exception e) {
            System.err.println("Failed to load stats: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void refreshStats(VBox totalRidesCard, VBox todayRidesCard, VBox earningsCard) {
        loadStats(totalRidesCard, todayRidesCard, earningsCard);
    }

    private void acceptRide(Booking ride) {
        try {
            ride.setDriverId(currentDriverId);
            ride.setBookingStatus(BookingStatus.DRIVER_ASSIGNED);
            
            String requestJson = gson.toJson(ride);
            ApiClient.put("/rides/" + ride.getBookingId() + "/accept", requestJson);
            
            loadAvailableRides();
            loadDriverRides();
            
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION
            );
            alert.setTitle("Success");
            alert.setHeaderText("Ride Accepted");
            alert.setContentText("You have successfully accepted the ride.");
            alert.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
            );
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Accept Ride");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
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
}