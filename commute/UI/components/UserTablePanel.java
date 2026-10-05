package com.CapitalCommute.commute.UI.components;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.CapitalCommute.commute.client.ApiClient;
import com.CapitalCommute.commute.model.Person;
import com.CapitalCommute.commute.model.enums.Gender;
import com.CapitalCommute.commute.util.DialogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
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

public class UserTablePanel extends VBox {

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

    private static final String BTN_TOGGLE =
        "-fx-background-color: " + WARNING + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 11px;" +
        "-fx-padding: 6 12 6 12;" +
        "-fx-cursor: hand;" +
        "-fx-background-radius: 4;";

    private TableView<Person> userTable;
    private Gson gson;
    private Label statusLabel;
    private String userType;

    public UserTablePanel(String userType) {
        this.userType = userType;
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>) (json, type, context) ->
                    LocalDateTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class,
                (JsonSerializer<LocalDateTime>) (src, type, context) ->
                    new JsonPrimitive(src.toString()))
            .create();
        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: " + BG_DARK + ";");

        String title = switch (userType) {
            case "client" -> "Clients";
            case "driver" -> "Drivers";
            default -> "All Users";
        };
        
        Label titleLabel = new Label(title);
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
        refreshButton.setOnAction(e -> loadUsers());

        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(titleLabel, refreshButton);

        userTable = new TableView<>();
        setupTable();

        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");

        this.getChildren().addAll(headerBox, userTable, statusLabel);
    }

    private void setupTable() {
        userTable.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );

        TableColumn<Person, Long> idCol = new TableColumn<>("National ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("nationalId"));
        idCol.setPrefWidth(150);

        TableColumn<Person, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(cellData -> {
            Person person = cellData.getValue();
            String fullName = person.getFirstName() + " " + 
                            (person.getMiddleName() != null ? person.getMiddleName() + " " : "") +
                            person.getLastName();
            return new javafx.beans.property.SimpleStringProperty(fullName);
        });
        nameCol.setPrefWidth(200);

        TableColumn<Person, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<Person, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setPrefWidth(130);

        TableColumn<Person, Gender> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        genderCol.setPrefWidth(100);

        TableColumn<Person, Boolean> activeCol = new TableColumn<>("Status");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        activeCol.setPrefWidth(100);
        activeCol.setCellFactory(column -> new TableCell<Person, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "Active" : "Inactive");
                    setStyle(
                        "-fx-text-fill: " + (item ? SUCCESS : DANGER) + ";" +
                        "-fx-font-weight: bold;"
                    );
                }
            }
        });

        TableColumn<Person, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(column -> new TableCell<Person, Void>() {
            private final Button viewButton = new Button("View");
            private final Button toggleButton = new Button("Toggle");

            {
                viewButton.setStyle(BTN_VIEW);
                toggleButton.setStyle(BTN_TOGGLE);

                viewButton.setOnAction(e -> {
                    Person person = getTableView().getItems().get(getIndex());
                    viewUserDetails(person);
                });

                toggleButton.setOnAction(e -> {
                    Person person = getTableView().getItems().get(getIndex());
                    toggleUserStatus(person);
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
                    buttons.getChildren().addAll(viewButton, toggleButton);
                    setGraphic(buttons);
                }
            }
        });

        userTable.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, genderCol, activeCol, actionsCol);
    }
    private void loadUsers() {
    try {
        statusLabel.setText("Loading users...");
        
        String endpoint;
        switch (userType) {
            case "driver":
                endpoint = "/admin/drivers";
                break;
            case "client":
                endpoint = "/admin/clients";
                break;
            default:
                endpoint = "/admin/users";
                break;
        }
        
        System.out.println("Loading from endpoint: " + endpoint);
        String json = ApiClient.get(endpoint);
        System.out.println("Response: " + json);
        
        Type listType = new TypeToken<List<Person>>(){}.getType();
        List<Person> users = gson.fromJson(json, listType);
        
        userTable.getItems().setAll(users);
        
        statusLabel.setText("Loaded " + users.size() + " user(s)");
        statusLabel.setStyle("-fx-text-fill: " + SUCCESS + ";");
        
    } catch (Exception e) {
        System.err.println("Error loading users: " + e.getMessage());
        e.printStackTrace();
        statusLabel.setText("Failed to load users: " + e.getMessage());
        statusLabel.setStyle("-fx-text-fill: " + DANGER + ";");
    }
}

    
    private void viewUserDetails(Person person) {
        String details = String.format(
            "National ID: %s\n" +
            "Name: %s %s %s\n" +
            "Email: %s\n" +
            "Phone: %s\n" +
            "Gender: %s\n" +
            "Date of Birth: %s\n" +
            "Residence: %s\n" +
            "Status: %s",
            person.getNationalId(),
            person.getFirstName(),
            person.getMiddleName() != null ? person.getMiddleName() : "",
            person.getLastName(),
            person.getEmail(),
            person.getPhoneNumber(),
            person.getGender(),
            person.getDateOfBirth(),
            person.getResidence(),
            person.isActive() ? "Active" : "Inactive"
        );
        
        DialogUtil.showInfo("User Details", "User Information", details);
    }

    private void toggleUserStatus(Person person) {
        String action = person.isActive() ? "deactivate" : "activate";
        boolean confirmed = DialogUtil.showConfirmation(
            "Toggle User Status",
            "Are you sure you want to " + action + " this user?",
            "User: " + person.getFirstName() + " " + person.getLastName()
        );

        if (confirmed) {
            DialogUtil.showInfo(
                "Feature Coming Soon",
                "Toggle Status",
                "User status toggle functionality will be available soon."
            );
        }
    }
}