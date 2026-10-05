package com.CapitalCommute.commute.UI.components;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

import com.CapitalCommute.commute.client.ApiClient;
import com.CapitalCommute.commute.model.Vehicle;
import com.CapitalCommute.commute.model.enums.VehicleStatus;
import com.CapitalCommute.commute.util.DialogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VehicleTablePanel extends VBox {

    

    private static final String BG_DARK = "#0f172a";
    private static final String BG_SURFACE = "#1e293b";
    private static final String ACCENT = "#3b82f6";
    private static final String SUCCESS = "#22c55e";
    private static final String WARNING = "#f59e0b";
    private static final String DANGER = "#ef4444";
    private static final String TEXT = "#f1f5f9";
    private static final String TEXT_MUTED = "#94a3b8";
    private static final String BORDER = "#475569";

    private static final String BTN_VIEW =
        "-fx-background-color: " + ACCENT + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 11px;" +
        "-fx-padding: 6 12 6 12;" +
        "-fx-cursor: hand;" +
        "-fx-background-radius: 4;";

    private static final String BTN_EDIT =
        "-fx-background-color: " + WARNING + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 11px;" +
        "-fx-padding: 6 12 6 12;" +
        "-fx-cursor: hand;" +
        "-fx-background-radius: 4;";

    private static final String BTN_DELETE =
        "-fx-background-color: " + DANGER + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 11px;" +
        "-fx-padding: 6 12 6 12;" +
        "-fx-cursor: hand;" +
        "-fx-background-radius: 4;";

    
    private TableView<Vehicle> vehicleTable;
    private Gson gson;
    private Label statusLabel;
    private boolean showActions;

    
    public VehicleTablePanel(boolean showActions) {
        this.showActions = showActions;
        this.gson = new Gson();
        initializeUI();
        loadVehicles();
    }

    
    private void initializeUI() {
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: " + BG_DARK + ";");

    
        Label titleLabel = new Label("Vehicles");
        titleLabel.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );

    
        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 8 16 8 16;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 6;"
        );
        refreshButton.setOnAction(e -> loadVehicles());

        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(titleLabel, refreshButton);

        
        vehicleTable = new TableView<>();
        setupTable();

        
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");

        this.getChildren().addAll(headerBox, vehicleTable, statusLabel);
    }

    
    private void setupTable() {
        vehicleTable.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );

    
        TableColumn<Vehicle, UUID> idCol = new TableColumn<>("Vehicle ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        idCol.setPrefWidth(250);

    
        TableColumn<Vehicle, String> plateCol = new TableColumn<>("License Plate");
        plateCol.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));
        plateCol.setPrefWidth(150);

    
        TableColumn<Vehicle, String> makeCol = new TableColumn<>("Make");
        makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));
        makeCol.setPrefWidth(120);

    
        TableColumn<Vehicle, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        modelCol.setPrefWidth(120);

    
        TableColumn<Vehicle, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        yearCol.setPrefWidth(80);

        
        TableColumn<Vehicle, VehicleStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("vehicleStatus"));
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(column -> new TableCell<Vehicle, VehicleStatus>() {
            @Override
            protected void updateItem(VehicleStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    String color = switch (item) {
                        case AVAILABLE -> SUCCESS;
                        case ON_TRIP -> ACCENT;
                        case UNDER_MAINTENANCE-> WARNING;
                        case OUT_OF_SERVICE -> DANGER;
                        default -> TEXT_MUTED;
                    };
                    setStyle(
                        "-fx-text-fill: " + color + ";" +
                        "-fx-font-weight: bold;"
                    );
                }
            }
        });

        vehicleTable.getColumns().addAll(idCol, plateCol, makeCol, modelCol, yearCol, statusCol);


        if (showActions) {
            TableColumn<Vehicle, Void> actionsCol = new TableColumn<>("Actions");
            actionsCol.setPrefWidth(250);
            actionsCol.setCellFactory(column -> new TableCell<Vehicle, Void>() {
                private final Button viewButton = new Button("View");
                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");

                {
                    viewButton.setStyle(BTN_VIEW);
                    editButton.setStyle(BTN_EDIT);
                    deleteButton.setStyle(BTN_DELETE);

                    viewButton.setOnAction(e -> {
                        Vehicle vehicle = getTableView().getItems().get(getIndex());
                        viewVehicleDetails(vehicle);
                    });

                    editButton.setOnAction(e -> {
                        Vehicle vehicle = getTableView().getItems().get(getIndex());
                        editVehicle(vehicle);
                    });

                    deleteButton.setOnAction(e -> {
                        Vehicle vehicle = getTableView().getItems().get(getIndex());
                        deleteVehicle(vehicle);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox buttons = new HBox(10);
                        buttons.setAlignment(Pos.CENTER);
                        buttons.getChildren().addAll(viewButton, editButton, deleteButton);
                        setGraphic(buttons);
                    }
                }
            });
            vehicleTable.getColumns().add(actionsCol);
        }
    }

    
    private void loadVehicles() {
    try {
        statusLabel.setText("Loading vehicles...");
        
        
        String json = ApiClient.get("/admin/vehicles");
        System.out.println("Vehicles response: " + json);
        
        Type listType = new TypeToken<List<Vehicle>>(){}.getType();
        List<Vehicle> vehicles = gson.fromJson(json, listType);
        
        vehicleTable.getItems().setAll(vehicles);
        
        statusLabel.setText("Loaded " + vehicles.size() + " vehicle(s)");
        statusLabel.setStyle("-fx-text-fill: " + SUCCESS + ";");
        
    } catch (Exception e) {
        System.err.println("Error loading vehicles: " + e.getMessage());
        e.printStackTrace();
        statusLabel.setText("Failed to load vehicles: " + e.getMessage());
        statusLabel.setStyle("-fx-text-fill: " + DANGER + ";");
    }
}
    
    private void viewVehicleDetails(Vehicle vehicle) {
        String details = String.format(
            "Vehicle ID: %s\n" +
            "License Plate: %s\n" +
            "Make: %s\n" +
            "Model: %s\n" +
            "Year: %d\n" +
            "Color: %s\n" +
            "Status: %s\n" +
            "Insurance: %s",
            vehicle.getVehicleId(),
            vehicle.getLicensePlate(),
            vehicle.getMake(),
            vehicle.getModel(),
            vehicle.getYear(),
            vehicle.getVehicleStatus(),
            vehicle.getInsuranceNumber()
        );
        
        DialogUtil.showSuccess("Vehicle Details", "Vehicle Information", details);
    }


    private void editVehicle(Vehicle vehicle) {
        DialogUtil.showSuccess(
            "Edit Vehicle",
            "Feature Coming Soon",
            "Vehicle editing functionality will be available soon."
        );
    }

    
    private void deleteVehicle(Vehicle vehicle) {
        boolean confirmed = DialogUtil.showConfirmation(
            "Delete Vehicle",
            "Are you sure you want to delete this vehicle?",
            "License Plate: " + vehicle.getLicensePlate()
        );

        if (confirmed) {
            try {
                
                ApiClient.delete("/vehicles/" + vehicle.getVehicleId());
                
                DialogUtil.showSuccess(
                    "Success",
                    "Vehicle Deleted",
                    "Vehicle has been deleted successfully."
                );
                
            
                loadVehicles();
                
            } catch (Exception e) {
                DialogUtil.showError(
                    "Error",
                    "Failed to delete vehicle",
                    e.getMessage()
                );
                e.printStackTrace();
            }
        }
    }
}