package com.CapitalCommute.commute.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.CapitalCommute.commute.client.ApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class GPSSimulationPanel extends BorderPane {

    private static final String BG_DARK    = "#0f172a";
    private static final String BG_SURFACE = "#1e293b";
    private static final String BG_SURFACE2= "#334155";
    private static final String ACCENT     = "#3b82f6";
    private static final String TEXT       = "#f1f5f9";
    private static final String TEXT_MUTED = "#94a3b8";
    private static final String SUCCESS    = "#22c55e";
    private static final String WARNING    = "#f59e0b";

    private static final double MIN_LAT  = -1.3500;
    private static final double MAX_LAT  = -1.2500;
    private static final double MIN_LNG  = 36.7500;
    private static final double MAX_LNG  = 36.9000;

    private static final int MAP_WIDTH   = 700;
    private static final int MAP_HEIGHT  = 550;

    private Canvas mapCanvas;
    private GraphicsContext gc;
    private Timeline animationTimer;
    private final Random random = new Random();
    private final Gson gson;
    private Label statusLabel;
    private Label activeTripsLabel;
    private TableView<VehicleMarker> vehicleTable;

    private final Map<String, double[]> vehiclePositions = new HashMap<>();
    private final List<VehicleMarker> markers = new ArrayList<>();

    private static final double[][] LANDMARKS = {
        {0.45, 0.50},
        {0.40, 0.45},
        {0.55, 0.55},
        {0.50, 0.65},
        {0.60, 0.40},
        {0.35, 0.60},
        {0.65, 0.60},
        {0.30, 0.50},
        {0.70, 0.45},
        {0.45, 0.35},
        {0.55, 0.35},
        {0.40, 0.70},
        {0.60, 0.70},
        {0.50, 0.30},
        {0.35, 0.40},
    };

    private static final String[] LANDMARK_NAMES = {
        "CBD", "Westlands", "Upperhill", "South B",
        "Eastleigh", "Lavington", "South C", "Kileleshwa",
        "Umoja", "Parklands", "Ngara", "Karen",
        "Embakasi", "Muthaiga", "Runda"
    };

    public GPSSimulationPanel() {
        this.gson = new GsonBuilder().create();
        initializeUI();
        loadActiveTrips();
        startAnimation();
    }

    private void initializeUI() {
        this.setStyle("-fx-background-color: " + BG_DARK + ";");
        this.setPadding(new Insets(20));

        Label titleLabel = new Label("GPS LIVE TRACKING");
        titleLabel.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + ACCENT + ";"
        );

        activeTripsLabel = new Label("Active Trips: 0");
        activeTripsLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + SUCCESS + ";" +
            "-fx-background-color: rgba(34,197,94,0.1);" +
            "-fx-padding: 4 12 4 12;" +
            "-fx-background-radius: 12;"
        );

        statusLabel = new Label("● LIVE");
        statusLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + SUCCESS + ";"
        );

        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 16 6 16;" +
            "-fx-cursor: hand;"
        );
        refreshButton.setOnAction(e -> loadActiveTrips());

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 15, 0));
        topBar.getChildren().addAll(
            titleLabel, activeTripsLabel, statusLabel, refreshButton
        );

        mapCanvas = new Canvas(MAP_WIDTH, MAP_HEIGHT);
        gc = mapCanvas.getGraphicsContext2D();
        drawBaseMap();

        VBox mapContainer = new VBox(5);
        mapContainer.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BG_SURFACE2 + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 16;"
        );

        Label mapLabel = new Label("Nairobi City Map");
        mapLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );

        mapContainer.getChildren().addAll(mapLabel, mapCanvas);

        vehicleTable = new TableView<>();
        vehicleTable.setPrefWidth(350);
        vehicleTable.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-border-color: " + BG_SURFACE2 + ";" +
            "-fx-border-radius: 8;"
        );

        TableColumn<VehicleMarker, String> idCol = new TableColumn<>("Vehicle");
        idCol.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        idCol.setPrefWidth(120);

        TableColumn<VehicleMarker, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        TableColumn<VehicleMarker, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationCol.setPrefWidth(130);

        vehicleTable.getColumns().addAll(idCol, statusCol, locationCol);

        VBox tableContainer = new VBox(10);
        tableContainer.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BG_SURFACE2 + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 16;"
        );

        Label tableLabel = new Label("Active Vehicles");
        tableLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );

        VBox legend = new VBox(8);
        legend.setPadding(new Insets(10, 0, 0, 0));

        legend.getChildren().addAll(
            createLegendItem("🟢", "Available Driver", SUCCESS),
            createLegendItem("🔵", "On Trip", ACCENT),
            createLegendItem("🟡", "Landmark", WARNING)
        );

        tableContainer.getChildren().addAll(tableLabel, vehicleTable, legend);

        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(0, 0, 0, 15));
        rightPanel.getChildren().add(tableContainer);

        HBox mainContent = new HBox(15);
        mainContent.getChildren().addAll(mapContainer, rightPanel);

        VBox root = new VBox(10);
        root.getChildren().addAll(topBar, mainContent);

        this.setCenter(root);
    }

    private void drawBaseMap() {
        gc.setFill(Color.web("#1e293b"));
        gc.fillRect(0, 0, MAP_WIDTH, MAP_HEIGHT);

        gc.setStroke(Color.web("#334155"));
        gc.setLineWidth(1.5);

        for (int i = 1; i < 10; i++) {
            double y = (MAP_HEIGHT / 10.0) * i;
            gc.strokeLine(0, y, MAP_WIDTH, y);
        }

        for (int i = 1; i < 10; i++) {
            double x = (MAP_WIDTH / 10.0) * i;
            gc.strokeLine(x, 0, x, MAP_HEIGHT);
        }

        gc.setStroke(Color.web("#475569"));
        gc.setLineWidth(3);

        gc.strokeLine(MAP_WIDTH * 0.5, 0, MAP_WIDTH * 0.7, MAP_HEIGHT * 0.5);
        gc.strokeLine(MAP_WIDTH * 0.5, MAP_HEIGHT, MAP_WIDTH * 0.7, MAP_HEIGHT * 0.5);
        gc.strokeLine(0, MAP_HEIGHT * 0.45, MAP_WIDTH * 0.5, MAP_HEIGHT * 0.5);
        gc.strokeLine(MAP_WIDTH * 0.5, MAP_HEIGHT, MAP_WIDTH * 0.2, MAP_HEIGHT * 0.6);
        gc.strokeLine(MAP_WIDTH * 0.5, 0, MAP_WIDTH * 0.5, MAP_HEIGHT);

        for (int i = 0; i < LANDMARKS.length; i++) {
            double x = LANDMARKS[i][0] * MAP_WIDTH;
            double y = LANDMARKS[i][1] * MAP_HEIGHT;

            gc.setFill(Color.web("#f59e0b", 0.6));
            gc.fillOval(x - 4, y - 4, 8, 8);

            gc.setFill(Color.web("#94a3b8"));
            gc.setFont(Font.font("Arial", 9));
            gc.fillText(LANDMARK_NAMES[i], x + 6, y + 4);
        }

        double cbdX = LANDMARKS[0][0] * MAP_WIDTH;
        double cbdY = LANDMARKS[0][1] * MAP_HEIGHT;
        gc.setFill(Color.web("#f59e0b"));
        gc.fillOval(cbdX - 6, cbdY - 6, 12, 12);
        gc.setFill(Color.web("#f1f5f9"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.fillText("CBD", cbdX + 8, cbdY + 4);
    }

    private void drawVehicles() {
        drawBaseMap();

        for (Map.Entry<String, double[]> entry : vehiclePositions.entrySet()) {
            String vehicleId = entry.getKey();
            double[] pos = entry.getValue();
            double x = pos[0];
            double y = pos[1];

            gc.setFill(Color.web(ACCENT, 0.3));
            gc.fillOval(x - 12, y - 12, 24, 24);

            gc.setFill(Color.web(ACCENT));
            gc.fillOval(x - 7, y - 7, 14, 14);

            gc.setFill(Color.WHITE);
            gc.fillOval(x - 3, y - 3, 6, 6);

            String shortId = vehicleId.length() > 8
                ? vehicleId.substring(0, 8) : vehicleId;
            gc.setFill(Color.web(TEXT));
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 9));
            gc.fillText("V-" + shortId, x + 10, y - 5);
        }
    }

    private void loadActiveTrips() {
        try {
            String responseJson = ApiClient.get("/bookings/active");

            if (responseJson == null || responseJson.isEmpty()
                    || responseJson.equals("[]")) {
                loadDemoVehicles();
                return;
            }

            java.lang.reflect.Type listType =
                new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> bookings =
                gson.fromJson(responseJson, listType);

            markers.clear();
            vehiclePositions.clear();

            for (Map<String, Object> booking : bookings) {
                String vehicleId = booking.get("vehicleId") != null
                    ? booking.get("vehicleId").toString() : "Unknown";

                double x = 50 + random.nextDouble() * (MAP_WIDTH - 100);
                double y = 50 + random.nextDouble() * (MAP_HEIGHT - 100);
                vehiclePositions.put(vehicleId, new double[]{x, y});

                String location = getNearestLandmark(x, y);

                markers.add(new VehicleMarker(
                    vehicleId.substring(0, Math.min(8, vehicleId.length())),
                    "ON TRIP",
                    location
                ));
            }

            activeTripsLabel.setText("Active Trips: " + markers.size());
            vehicleTable.getItems().setAll(markers);
            drawVehicles();

        } catch (Exception e) {
            loadDemoVehicles();
        }
    }

    private void loadDemoVehicles() {
        markers.clear();
        vehiclePositions.clear();

        String[] demoIds = {"KCA123A", "KCB456B", "KCC789C", "KCD012D"};
        String[] demoStatuses = {"ON TRIP", "ON TRIP", "AVAILABLE", "ON TRIP"};

        for (int i = 0; i < demoIds.length; i++) {
            double x = 100 + random.nextDouble() * (MAP_WIDTH - 200);
            double y = 100 + random.nextDouble() * (MAP_HEIGHT - 200);
            vehiclePositions.put(demoIds[i], new double[]{x, y});

            String location = getNearestLandmark(x, y);
            markers.add(new VehicleMarker(demoIds[i], demoStatuses[i], location));
        }

        activeTripsLabel.setText("Demo Mode: " + markers.size() + " vehicles");
        vehicleTable.getItems().setAll(markers);
        drawVehicles();
    }

    private void startAnimation() {
        animationTimer = new Timeline(
            new KeyFrame(Duration.seconds(2), e -> moveVehicles())
        );
        animationTimer.setCycleCount(Timeline.INDEFINITE);
        animationTimer.play();
    }

    private void moveVehicles() {
        for (Map.Entry<String, double[]> entry : vehiclePositions.entrySet()) {
            double[] pos = entry.getValue();

            double dx = (random.nextDouble() - 0.5) * 15;
            double dy = (random.nextDouble() - 0.5) * 15;

            pos[0] = Math.max(20, Math.min(MAP_WIDTH - 20, pos[0] + dx));
            pos[1] = Math.max(20, Math.min(MAP_HEIGHT - 20, pos[1] + dy));

            String vehicleId = entry.getKey();
            String shortId = vehicleId.length() > 8
                ? vehicleId.substring(0, 8) : vehicleId;
            String newLocation = getNearestLandmark(pos[0], pos[1]);

            for (VehicleMarker marker : markers) {
                if (marker.getVehicleId().equals(shortId)) {
                    marker.setLocation(newLocation);
                    break;
                }
            }
        }

        drawVehicles();
        vehicleTable.refresh();
    }

    private String getNearestLandmark(double x, double y) {
        double minDist = Double.MAX_VALUE;
        int nearest = 0;

        for (int i = 0; i < LANDMARKS.length; i++) {
            double lx = LANDMARKS[i][0] * MAP_WIDTH;
            double ly = LANDMARKS[i][1] * MAP_HEIGHT;
            double dist = Math.sqrt(Math.pow(x - lx, 2) + Math.pow(y - ly, 2));
            if (dist < minDist) {
                minDist = dist;
                nearest = i;
            }
        }
        return "Near " + LANDMARK_NAMES[nearest];
    }

    private HBox createLegendItem(String icon, String text, String color) {
        Label iconLabel = new Label(icon);
        Label textLabel = new Label(text);
        textLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + color + ";"
        );
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);
        item.getChildren().addAll(iconLabel, textLabel);
        return item;
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    public static class VehicleMarker {
        private String vehicleId;
        private String status;
        private String location;

        public VehicleMarker(String vehicleId, String status, String location) {
            this.vehicleId = vehicleId;
            this.status = status;
            this.location = location;
        }

        public String getVehicleId() { return vehicleId; }
        public String getStatus()    { return status; }
        public String getLocation()  { return location; }

        public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
        public void setStatus(String status)       { this.status = status; }
        public void setLocation(String location)   { this.location = location; }
    }
}