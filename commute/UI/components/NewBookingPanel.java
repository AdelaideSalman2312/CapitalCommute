package com.CapitalCommute.commute.UI.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.CapitalCommute.commute.client.ApiClient;
import com.CapitalCommute.commute.model.Booking;
import com.CapitalCommute.commute.model.enums.BookingStatus;
import com.CapitalCommute.commute.util.DialogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class NewBookingPanel extends VBox {

   
    private static final String BG_DARK = "#0f172a";
    private static final String BG_SURFACE = "#1e293b";
    private static final String BG_SURFACE2 = "#334155";
    private static final String ACCENT = "#3b82f6";
    private static final String SUCCESS = "#22c55e";
    private static final String TEXT = "#f1f5f9";
    private static final String TEXT_MUTED = "#94a3b8";
    private static final String BORDER = "#475569";

    private static final String FIELD_STYLE =
        "-fx-background-color: " + BG_SURFACE2 + ";" +
        "-fx-border-color: " + BORDER + ";" +
        "-fx-border-radius: 8;" +
        "-fx-background-radius: 8;" +
        "-fx-text-fill: " + TEXT + ";" +
        "-fx-font-size: 13px;" +
        "-fx-padding: 10 14 10 14;" +
        "-fx-pref-width: 400px;" +
        "-fx-prompt-text-fill: #64748b;";

    private static final String BTN_PRIMARY =
        "-fx-background-color: " + ACCENT + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 14px;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 8;" +
        "-fx-padding: 12 32 12 32;" +
        "-fx-cursor: hand;";

    private static final String BTN_SECONDARY =
        "-fx-background-color: " + BG_SURFACE2 + ";" +
        "-fx-text-fill: " + TEXT + ";" +
        "-fx-font-size: 14px;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 8;" +
        "-fx-padding: 12 32 12 32;" +
        "-fx-cursor: hand;" +
        "-fx-border-color: " + BORDER + ";" +
        "-fx-border-width: 1;" +
        "-fx-border-radius: 8;";

   
    private TextField pickupField;
    private TextField dropoffField;
    private DatePicker pickupDatePicker;
    private TextField pickupTimeField;
    private TextField distanceField;
    private Label fareLabel;
    private Label messageLabel;
    
    private UUID clientId;
    private Gson gson;

    
    public NewBookingPanel(UUID clientId) {
        this.clientId = clientId;
        this.gson = new GsonBuilder()
           .registerTypeAdapter(LocalDate.class,
        (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
            new JsonPrimitive(src.toString()))
    .registerTypeAdapter(LocalDate.class,
        (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
            LocalDate.parse(json.getAsString()))
    .registerTypeAdapter(LocalDateTime.class,
        (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
            new JsonPrimitive(src.toString()))
    .registerTypeAdapter(LocalDateTime.class,
        (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
            LocalDateTime.parse(json.getAsString()))
    .create(); 
        initializeUI();
    }

    
    private void initializeUI() {
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: " + BG_DARK + ";");

        
        Label titleLabel = new Label("Book a New Ride");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );

        Label subtitleLabel = new Label("Fill in the details below to book your ride");
        subtitleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );

       
        VBox contentBox = createFormContent();

        
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;"
        );
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        this.getChildren().addAll(titleLabel, subtitleLabel, scrollPane);
    }

    
    private VBox createFormContent() {
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(0, 20, 20, 0)); 
        VBox formCard = new VBox(25);
        formCard.setPadding(new Insets(30));
        formCard.setMaxWidth(700);
        formCard.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );

       
        VBox locationSection = createSection("📍 Location Details");
        
        GridPane locationGrid = new GridPane();
        locationGrid.setHgap(20);
        locationGrid.setVgap(15);

        Label pickupLabel = createLabel("Pickup Location *");
        pickupField = new TextField();
        pickupField.setPromptText("e.g., Nairobi CBD, Kenyatta Avenue");
        pickupField.setStyle(FIELD_STYLE);

        Label dropoffLabel = createLabel("Dropoff Location *");
        dropoffField = new TextField();
        dropoffField.setPromptText("e.g., Westlands, ABC Place");
        dropoffField.setStyle(FIELD_STYLE);

        locationGrid.add(pickupLabel, 0, 0);
        locationGrid.add(pickupField, 0, 1);
        locationGrid.add(dropoffLabel, 0, 2);
        locationGrid.add(dropoffField, 0, 3);

        locationSection.getChildren().add(locationGrid);

        
        VBox datetimeSection = createSection("🕒 Pickup Date & Time");
        
        GridPane datetimeGrid = new GridPane();
        datetimeGrid.setHgap(20);
        datetimeGrid.setVgap(15);

        Label dateLabel = createLabel("Pickup Date *");
        pickupDatePicker = new DatePicker();
        pickupDatePicker.setPromptText("Select date");
        pickupDatePicker.setStyle(FIELD_STYLE);

        Label timeLabel = createLabel("Pickup Time (24-hour format) *");
        pickupTimeField = new TextField();
        pickupTimeField.setPromptText("e.g., 14:30");
        pickupTimeField.setStyle(FIELD_STYLE);

        Label timeHintLabel = new Label("💡 Use format HH:MM (e.g., 09:00, 14:30, 18:45)");
        timeHintLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-font-style: italic;"
        );

        datetimeGrid.add(dateLabel, 0, 0);
        datetimeGrid.add(pickupDatePicker, 0, 1);
        datetimeGrid.add(timeLabel, 0, 2);
        datetimeGrid.add(pickupTimeField, 0, 3);
        datetimeGrid.add(timeHintLabel, 0, 4);

        datetimeSection.getChildren().add(datetimeGrid);

        // Section 3: Distance & Fare
        VBox fareSection = createSection("💰 Distance & Fare");
        
        GridPane fareGrid = new GridPane();
        fareGrid.setHgap(20);
        fareGrid.setVgap(15);

        Label distanceLabel = createLabel("Estimated Distance (km) *");
        distanceField = new TextField();
        distanceField.setPromptText("e.g., 15.5");
        distanceField.setStyle(FIELD_STYLE);
        distanceField.textProperty().addListener((obs, old, newVal) -> calculateFare());

        Label fareTextLabel = createLabel("Estimated Fare");
        fareLabel = new Label("KES 0.00");
        fareLabel.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + SUCCESS + ";"
        );

        Label fareInfoLabel = new Label("💡 Fare = Base Rate (KES 500) + KES 100 per km");
        fareInfoLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-font-style: italic;"
        );

        fareGrid.add(distanceLabel, 0, 0);
        fareGrid.add(distanceField, 0, 1);
        fareGrid.add(fareTextLabel, 0, 2);
        fareGrid.add(fareLabel, 0, 3);
        fareGrid.add(fareInfoLabel, 0, 4);

        fareSection.getChildren().add(fareGrid);

        // Buttons
        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button submitButton = new Button("🚗 Book Ride Now");
        submitButton.setStyle(BTN_PRIMARY);
        submitButton.setOnAction(e -> createBooking());

        Button clearButton = new Button("Clear Form");
        clearButton.setStyle(BTN_SECONDARY);
        clearButton.setOnAction(e -> clearForm());

        buttonBox.getChildren().addAll(submitButton, clearButton);

        // Message Label
        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(650);

        formCard.getChildren().addAll(
            locationSection,
            createDivider(),
            datetimeSection,
            createDivider(),
            fareSection,
            createDivider(),
            buttonBox,
            messageLabel
        );

        contentBox.getChildren().add(formCard);
        return contentBox;
    }

    
    private VBox createSection(String title) {
        VBox section = new VBox(15);
        
        Label sectionTitle = new Label(title);
        sectionTitle.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );
        
        section.getChildren().add(sectionTitle);
        return section;
    }

    
    private javafx.scene.layout.Region createDivider() {
        javafx.scene.layout.Region divider = new javafx.scene.layout.Region();
        divider.setPrefHeight(1);
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-background-color: " + BORDER + ";");
        return divider;
    }

    
    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-font-weight: 500;"
        );
        return label;
    }

    
    private void calculateFare() {
        try {
            String distanceText = distanceField.getText().trim();
            if (!distanceText.isEmpty()) {
                double distance = Double.parseDouble(distanceText);
                
                
                double fare = 500 + (distance * 100);
                
                fareLabel.setText(String.format("KES %.2f", fare));
            } else {
                fareLabel.setText("KES 0.00");
            }
        } catch (NumberFormatException e) {
            fareLabel.setText("KES 0.00");
        }
    }

    
    private void createBooking() {
        try {
            
            if (!validateInputs()) {
                return;
            }

            
            String dateStr = pickupDatePicker.getValue().toString();
            String timeStr = pickupTimeField.getText().trim();
            LocalDateTime pickupTime = LocalDateTime.parse(dateStr + "T" + timeStr + ":00");

            
            double distance = Double.parseDouble(distanceField.getText().trim());
            double fare = 500 + (distance * 100);

            
            Booking booking = new Booking();
            booking.setClientId(clientId);
            booking.setPickupLocation(pickupField.getText().trim());
            booking.setDropoffLocation(dropoffField.getText().trim());
            booking.setPickupTime(pickupTime);
            booking.setDistance(distance);
            booking.setFareAmount(fare);
            booking.setBookingStatus(BookingStatus.CLIENT_REQUESTED);

            
            String json = gson.toJson(booking);

            
            String response = ApiClient.post("/bookings", json);

    
            DialogUtil.showSuccess(
                "Success",
                "Booking Created",
                "Your ride has been booked successfully!\n\n" +
                "Pickup: " + pickupField.getText() + "\n" +
                "Dropoff: " + dropoffField.getText() + "\n" +
                "Fare: KES " + String.format("%.2f", fare)
            );

            
            clearForm();

        } catch (Exception e) {
            showError("Failed to create booking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    private boolean validateInputs() {
        if (pickupField.getText().trim().isEmpty()) {
            showError("❌ Pickup location is required");
            return false;
        }
        if (dropoffField.getText().trim().isEmpty()) {
            showError("❌ Dropoff location is required");
            return false;
        }
        if (pickupDatePicker.getValue() == null) {
            showError("❌ Pickup date is required");
            return false;
        }
        if (pickupTimeField.getText().trim().isEmpty()) {
            showError("❌ Pickup time is required");
            return false;
        }
        if (!pickupTimeField.getText().matches("\\d{2}:\\d{2}")) {
            showError("❌ Invalid time format. Use HH:MM (e.g., 14:30)");
            return false;
        }
        if (distanceField.getText().trim().isEmpty()) {
            showError("❌ Distance is required");
            return false;
        }
        try {
            double distance = Double.parseDouble(distanceField.getText().trim());
            if (distance <= 0) {
                showError("❌ Distance must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("❌ Distance must be a valid number");
            return false;
        }
        return true;
    }


    private void clearForm() {
        pickupField.clear();
        dropoffField.clear();
        pickupDatePicker.setValue(null);
        pickupTimeField.clear();
        distanceField.clear();
        fareLabel.setText("KES 0.00");
        messageLabel.setText("");
    }

    
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setTextFill(Color.web("#ef4444"));
    }
}