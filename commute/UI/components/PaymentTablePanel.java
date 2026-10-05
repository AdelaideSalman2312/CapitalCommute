
package com.CapitalCommute.commute.UI.components;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.CapitalCommute.commute.client.ApiClient;
import com.CapitalCommute.commute.model.Payment;
import com.CapitalCommute.commute.model.enums.PaymentMethod;
import com.CapitalCommute.commute.model.enums.PaymentStatus;
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

public class PaymentTablePanel extends VBox {

    
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

    
    private TableView<Payment> paymentTable;
    private Gson gson;
    private UUID clientId;
    private Label statusLabel;
    private Label totalLabel;

    
    public PaymentTablePanel(UUID clientId) {
        this.clientId = clientId;
        this.gson = createGson();
        initializeUI();
        loadPayments();
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

        
        HBox headerRow = new HBox(20);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Payment History");
        titleLabel.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );

        
        VBox totalCard = new VBox(5);
        totalCard.setPadding(new Insets(15));
        totalCard.setAlignment(Pos.CENTER);
        totalCard.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + SUCCESS + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;"
        );

        Label totalTextLabel = new Label("Total Spent");
        totalTextLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );

        totalLabel = new Label("KES 0.00");
        totalLabel.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + SUCCESS + ";"
        );

        totalCard.getChildren().addAll(totalTextLabel, totalLabel);

        
        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setStyle(
            "-fx-background-color: " + ACCENT + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 8 16 8 16;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 6;"
        );
        refreshButton.setOnAction(e -> loadPayments());

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        headerRow.getChildren().addAll(titleLabel, spacer, totalCard, refreshButton);

        
        paymentTable = new TableView<>();
        setupTable();

        
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");

        this.getChildren().addAll(headerRow, paymentTable, statusLabel);
    }

    
    private void setupTable() {
        paymentTable.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );

        
        TableColumn<Payment, UUID> idCol = new TableColumn<>("Payment ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        idCol.setPrefWidth(250);

        
        TableColumn<Payment, UUID> bookingCol = new TableColumn<>("Booking ID");
        bookingCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        bookingCol.setPrefWidth(250);

        
        TableColumn<Payment, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("fareAmount"));
        amountCol.setPrefWidth(120);
        amountCol.setCellFactory(column -> new TableCell<Payment, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("KES %.2f", item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: " + SUCCESS + ";");
                }
            }
        });

        TableColumn<Payment, PaymentMethod> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        methodCol.setPrefWidth(120);

        
        TableColumn<Payment, PaymentStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(column -> new TableCell<Payment, PaymentStatus>() {
            @Override
            protected void updateItem(PaymentStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    String color = switch (item) {
                        case COMPLETED -> SUCCESS;
                        case PENDING -> WARNING;
                        case FAILED -> DANGER;
                        default -> TEXT_MUTED;
                    };
                    setStyle(
                        "-fx-text-fill: " + color + ";" +
                        "-fx-font-weight: bold;"
                    );
                }
            }
        });

        
        TableColumn<Payment, LocalDateTime> timeCol = new TableColumn<>("Payment Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("paymentTime"));
        timeCol.setPrefWidth(150);
        timeCol.setCellFactory(column -> new TableCell<Payment, LocalDateTime>() {
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

        
        TableColumn<Payment, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(100);
        actionsCol.setCellFactory(column -> new TableCell<Payment, Void>() {
            private final Button viewButton = new Button("View");

            {
                viewButton.setStyle(BTN_VIEW);
                viewButton.setOnAction(e -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    viewPaymentDetails(payment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });

        paymentTable.getColumns().addAll(idCol, bookingCol, amountCol, methodCol, statusCol, timeCol, actionsCol);
    }

    
    private void loadPayments() {
        try {
            statusLabel.setText("Loading payments...");
            
            
            String json = ApiClient.get("/admin/payments");
            System.out.println("Payments response: " + json);
            
            Type listType = new TypeToken<List<Payment>>(){}.getType();
            List<Payment> allPayments = gson.fromJson(json, listType);
            
            
            paymentTable.getItems().setAll(allPayments);
            
    
            double total = allPayments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Payment::getFareAmount)
                .sum();
            
            totalLabel.setText(String.format("KES %.2f", total));
            
            statusLabel.setText("Loaded " + allPayments.size() + " payment(s)");
            statusLabel.setStyle("-fx-text-fill: " + SUCCESS + ";");
            
        } catch (Exception e) {
            statusLabel.setText("Failed to load payments: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: " + DANGER + ";");
            e.printStackTrace();
        }
    }

    
    private void viewPaymentDetails(Payment payment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        
        String details = String.format(
            "Payment ID: %s\n" +
            "Booking ID: %s\n" +
            "Amount: KES %.2f\n" +
            "Payment Method: %s\n" +
            "Status: %s\n" +
            "Payment Time: %s",
            payment.getPaymentId(),
            payment.getBookingId(),
            payment.getFareAmount(),
            payment.getPaymentMethod(),
            payment.getPaymentStatus(),
            payment.getPaymentTime().format(formatter)
        );
        
        DialogUtil.showSuccess("Payment Details", "Payment Information", details);
    }
}