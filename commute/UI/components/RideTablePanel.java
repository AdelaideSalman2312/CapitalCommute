package com.CapitalCommute.commute.UI.components;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.CapitalCommute.commute.client.ApiClient;
import com.CapitalCommute.commute.model.Ride;
import com.CapitalCommute.commute.model.enums.RideStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class RideTablePanel extends VBox {
    
    private TableView<Ride> rideTable;
    private Gson gson;
    private UUID driverId;
    private String filterStatus;
    private static final String BG_SURFACE = "#1e293b";
    private static final String BORDER = "#475569";
    private static final String ACCENT = "#3b82f6";
    private static final String SUCCESS = "#22c55e";
    private static final String WARNING = "#f59e0b";
    private static final String ERROR = "#ef4444";
    
    public RideTablePanel(UUID driverId) {
        this(driverId, null);
    }
    
    public RideTablePanel(UUID driverId, String filterStatus) {
        this.driverId = driverId;
        this.filterStatus = filterStatus;
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, 
                (com.google.gson.JsonDeserializer<LocalDateTime>) (json, type, context) -> 
                    LocalDateTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class,
                (com.google.gson.JsonSerializer<LocalDateTime>) (src, type, context) -> 
                    new com.google.gson.JsonPrimitive(src.toString()))
            .create();
        
        initializeUI();
        loadData();
    }
    
    private void initializeUI() {
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        
        createTable();
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16 8 16;" +
            "-fx-cursor: hand;"
        );
        refreshBtn.setOnAction(e -> loadData());
        
        this.getChildren().addAll(rideTable, refreshBtn);
    }
    
    private void createTable() {
        rideTable = new TableView<>();
        rideTable.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;"
        );
        
        TableColumn<Ride, String> rideIdCol = new TableColumn<>("Ride ID");
        rideIdCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRideId().toString().substring(0, 8)));
        rideIdCol.setPrefWidth(100);
        
        TableColumn<Ride, String> clientIdCol = new TableColumn<>("Client");
        clientIdCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClientId().toString().substring(0, 8)));
        clientIdCol.setPrefWidth(100);
        
        TableColumn<Ride, String> pickupCol = new TableColumn<>("Pickup");
        pickupCol.setCellValueFactory(new PropertyValueFactory<>("pickupLocation"));
        pickupCol.setPrefWidth(180);
        
        TableColumn<Ride, String> dropoffCol = new TableColumn<>("Dropoff");
        dropoffCol.setCellValueFactory(new PropertyValueFactory<>("dropoffLocation"));
        dropoffCol.setPrefWidth(180);
        
        TableColumn<Ride, Double> fareCol = new TableColumn<>("Fare");
        fareCol.setCellValueFactory(new PropertyValueFactory<>("fare"));
        fareCol.setPrefWidth(100);
        fareCol.setCellFactory(tc -> new javafx.scene.control.TableCell<Ride, Double>() {
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
        
        TableColumn<Ride, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRideStatus().toString()));
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(tc -> new javafx.scene.control.TableCell<Ride, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("PENDING")) {
                        setStyle("-fx-text-fill: " + WARNING + "; -fx-font-weight: bold;");
                    } else if (status.equals("ACCEPTED")) {
                        setStyle("-fx-text-fill: " + ACCENT + "; -fx-font-weight: bold;");
                    } else if (status.equals("IN_PROGRESS")) {
                        setStyle("-fx-text-fill: " + SUCCESS + "; -fx-font-weight: bold;");
                    } else if (status.equals("COMPLETED")) {
                        setStyle("-fx-text-fill: " + SUCCESS + ";");
                    } else if (status.equals("CANCELLED")) {
                        setStyle("-fx-text-fill: " + ERROR + ";");
                    }
                }
            }
        });
        
        TableColumn<Ride, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getRideDate();
            if (date != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return new javafx.beans.property.SimpleStringProperty(date.format(formatter));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        dateCol.setPrefWidth(150);
        
        TableColumn<Ride, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(100);
        actionCol.setCellFactory(tc -> new javafx.scene.control.TableCell<Ride, Void>() {
            private final Button acceptBtn = new Button("Accept");
            private final Button startBtn = new Button("Start");
            private final Button completeBtn = new Button("Complete");
            
            {
                acceptBtn.setStyle(
                    "-fx-background-color: " + SUCCESS + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 4;" +
                    "-fx-padding: 4 8 4 8;" +
                    "-fx-cursor: hand;" +
                    "-fx-font-size: 11px;"
                );
                acceptBtn.setOnAction(event -> {
                    Ride ride = getTableView().getItems().get(getIndex());
                    updateRideStatus(ride, "ACCEPTED");
                });
                
                startBtn.setStyle(
                    "-fx-background-color: " + ACCENT + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 4;" +
                    "-fx-padding: 4 8 4 8;" +
                    "-fx-cursor: hand;" +
                    "-fx-font-size: 11px;"
                );
                startBtn.setOnAction(event -> {
                    Ride ride = getTableView().getItems().get(getIndex());
                    updateRideStatus(ride, "IN_PROGRESS");
                });
                
                completeBtn.setStyle(
                    "-fx-background-color: " + SUCCESS + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 4;" +
                    "-fx-padding: 4 8 4 8;" +
                    "-fx-cursor: hand;" +
                    "-fx-font-size: 11px;"
                );
                completeBtn.setOnAction(event -> {
                    Ride ride = getTableView().getItems().get(getIndex());
                    updateRideStatus(ride, "COMPLETED");
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Ride ride = getTableView().getItems().get(getIndex());
                    RideStatus status = ride.getRideStatus();
                    
                    if (status == RideStatus.PENDING) {
                        setGraphic(acceptBtn);
                    } else if (status == RideStatus.ACCEPTED) {
                        setGraphic(startBtn);
                    } else if (status == RideStatus.IN_PROGRESS) {
                        setGraphic(completeBtn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        
        rideTable.getColumns().addAll(rideIdCol, clientIdCol, pickupCol, dropoffCol, fareCol, statusCol, dateCol, actionCol);
        rideTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void loadData() {
        try {
            String endpoint;
            if (filterStatus != null && !filterStatus.isEmpty()) {
                endpoint = "/rides/driver/" + driverId + "/status/" + filterStatus;
            } else {
                endpoint = "/rides/driver/" + driverId;
            }
            
            String response = ApiClient.get(endpoint);
            java.lang.reflect.Type listType = new TypeToken<List<Ride>>(){}.getType();
            List<Ride> rides = gson.fromJson(response, listType);
            rideTable.getItems().setAll(rides);
        } catch (Exception e) {
            showError("Failed to load rides: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateRideStatus(Ride ride, String status) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Update Status");
        alert.setHeaderText("Confirm Status Update");
        alert.setContentText("Mark this ride as " + status + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String requestJson = "{\"status\": \"" + status + "\"}";
                String response = ApiClient.put("/rides/" + ride.getRideId() + "/status", requestJson);
                
                JsonObject json = JsonParser.parseString(response).getAsJsonObject();
                if (json.has("message")) {
                    showSuccess(json.get("message").getAsString());
                    loadData();
                }
            } catch (Exception e) {
                showError("Failed to update status: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public void refresh() {
        loadData();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}