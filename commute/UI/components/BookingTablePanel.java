package com.CapitalCommute.commute.UI.components;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.CapitalCommute.commute.client.ApiClient;
import com.CapitalCommute.commute.model.Booking;
import com.CapitalCommute.commute.model.enums.BookingStatus;
import com.CapitalCommute.commute.util.DialogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
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

public class BookingTablePanel extends VBox {

    private static final String BG_DARK = "#0f172a";
    private static final String BG_SURFACE = "#1e293b";
    private static final String BG_SURFACE2 = "#334155";
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

    private static final String BTN_CANCEL =
        "-fx-background-color: " + DANGER + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 11px;" +
        "-fx-padding: 6 12 6 12;" +
        "-fx-cursor: hand;" +
        "-fx-background-radius: 4;";

    private TableView<Booking> bookingTable;
    private Gson gson;
    private UUID clientId;
    private Label statusLabel;
    public BookingTablePanel() {
    this.clientId = null;
    this.gson = createGson();
    initializeUI();
    loadBookings();
}
    

        
    private Gson createGson() {
        return new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, 
                (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString()))
            .create();
    }

    private void initializeUI() {
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: " + BG_DARK + ";");

        Label titleLabel = new Label("My Bookings");
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
        refreshButton.setOnAction(e -> loadBookings());

        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(titleLabel, refreshButton);

        bookingTable = new TableView<>();
        setupTable();

        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");

        this.getChildren().addAll(headerBox, bookingTable, statusLabel);
    }

    private void setupTable() {
        bookingTable.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );

        TableColumn<Booking, UUID> idCol = new TableColumn<>("Booking ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        idCol.setPrefWidth(250);

        TableColumn<Booking, String> clientIdCol = new TableColumn<>("Client ID");
        clientIdCol.setCellValueFactory(cellData -> {
            UUID clientId = cellData.getValue().getClientId();
            String displayId = clientId != null ? clientId.toString().substring(0, 8) : "N/A";
            return new javafx.beans.property.SimpleStringProperty(displayId);
        });
        clientIdCol.setPrefWidth(100);

        TableColumn<Booking, String> pickupCol = new TableColumn<>("Pickup");
        pickupCol.setCellValueFactory(new PropertyValueFactory<>("pickupLocation"));
        pickupCol.setPrefWidth(150);

        TableColumn<Booking, String> dropoffCol = new TableColumn<>("Dropoff");
        dropoffCol.setCellValueFactory(new PropertyValueFactory<>("dropoffLocation"));
        dropoffCol.setPrefWidth(150);

        TableColumn<Booking, LocalDateTime> pickupTimeCol = new TableColumn<>("Pickup Time");
        pickupTimeCol.setCellValueFactory(new PropertyValueFactory<>("pickupTime"));
        pickupTimeCol.setPrefWidth(150);
        pickupTimeCol.setCellFactory(column -> new TableCell<Booking, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
                    setText(item.format(formatter));
                }
            }
        });

        TableColumn<Booking, BookingStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("bookingStatus"));
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(column -> new TableCell<Booking, BookingStatus>() {
            @Override
            protected void updateItem(BookingStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    String color = switch (item) {
                        case CLIENT_REQUESTED -> WARNING;
                        case DRIVER_ASSIGNED -> ACCENT;
                        case TRIP_STARTED -> SUCCESS;
                        case TRIP_COMPLETED -> SUCCESS;
                        case CANCELLED_BY_CLIENT -> DANGER;
                        case CANCELLED_BY_DRIVER -> DANGER;
                        default -> TEXT_MUTED;
                    };
                    setStyle(
                        "-fx-text-fill: " + color + ";" +
                        "-fx-font-weight: bold;"
                    );
                }
            }
        });

        TableColumn<Booking, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(column -> new TableCell<Booking, Void>() {
            private final Button viewButton = new Button("View");
            private final Button cancelButton = new Button("Cancel");

            {
                viewButton.setStyle(BTN_VIEW);
                cancelButton.setStyle(BTN_CANCEL);

                viewButton.setOnAction(e -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    viewBookingDetails(booking);
                });

                cancelButton.setOnAction(e -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    cancelBooking(booking);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(10);
                    buttons.setAlignment(Pos.CENTER);
                    
                    buttons.getChildren().add(viewButton);
                    
                    if (booking.getBookingStatus() == BookingStatus.CLIENT_REQUESTED ||
                        booking.getBookingStatus() == BookingStatus.DRIVER_ASSIGNED) {
                        buttons.getChildren().add(cancelButton);
                    }
                    
                    setGraphic(buttons);
                }
            }
        });

        bookingTable.getColumns().addAll(idCol, clientIdCol, pickupCol, dropoffCol, pickupTimeCol, statusCol, actionsCol);
    }

    

private void loadBookings() {
    try {
        statusLabel.setText("Loading bookings...");
        

        String endpoint = clientId != null ? "/bookings/client/" + clientId.toString() : "/admin/bookings";
        String json = ApiClient.get(endpoint);
        System.out.println("Bookings response: " + json);
        
        Type listType = new TypeToken<List<Booking>>(){}.getType();
        List<Booking> bookings = gson.fromJson(json, listType);
        
        bookingTable.getItems().setAll(bookings);
        
        statusLabel.setText("Loaded " + bookings.size() + " booking(s)");
        statusLabel.setStyle("-fx-text-fill: " + SUCCESS + ";");
        
    } catch (Exception e) {
        System.err.println("Error loading bookings: " + e.getMessage());
        e.printStackTrace();
        statusLabel.setText("Failed to load bookings: " + e.getMessage());
        statusLabel.setStyle("-fx-text-fill: " + DANGER + ";");
    }
}

    private void viewBookingDetails(Booking booking) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        
        String details = String.format(
            "Booking ID: %s\n" +
            "Pickup: %s\n" +
            "Dropoff: %s\n" +
            "Pickup Time: %s\n" +
            "Status: %s\n" +
            "Fare: KES %.2f",
            booking.getBookingId(),
            booking.getPickupLocation(),
            booking.getDropoffLocation(),
            booking.getPickupTime() != null ? booking.getPickupTime().format(formatter) : "N/A",
            booking.getBookingStatus(),
            booking.getFareAmount()
        );
        
        DialogUtil.showSuccess("Booking Details", "Booking Information", details);
    }

    private void cancelBooking(Booking booking) {
        boolean confirmed = DialogUtil.showConfirmation(
            "Cancel Booking",
            "Are you sure you want to cancel this booking?",
            "Booking ID: " + booking.getBookingId()
        );

        if (confirmed) {
            try {
                booking.setBookingStatus(BookingStatus.CANCELLED_BY_CLIENT);
                
                String json = gson.toJson(booking);
                
                ApiClient.put("/bookings/" + booking.getBookingId(), json);
                
                DialogUtil.showSuccess(
                    "Success",
                    "Booking Cancelled",
                    "Your booking has been cancelled successfully."
                );
                
                loadBookings();
                
            } catch (Exception e) {
                DialogUtil.showError(
                    "Error",
                    "Failed to cancel booking",
                    e.getMessage()
                );
                e.printStackTrace();
            }
        }
    }
}