
package com.CapitalCommute.commute.UI.components;

import com.CapitalCommute.commute.client.ApiClient;
import com.CapitalCommute.commute.model.Client;
import com.CapitalCommute.commute.model.enums.Gender;
import com.CapitalCommute.commute.util.DialogUtil;
import com.CapitalCommute.commute.util.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ProfilePanel extends VBox {

    
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

    
    private TextField nationalIdField;
    private TextField firstNameField;
    private TextField middleNameField;
    private TextField lastNameField;
    private DatePicker dateOfBirthPicker;
    private TextField residenceField;
    private TextField passportField;
    private TextField emailField;
    private TextField phoneField;
    private ComboBox<Gender> genderCombo;
    private Label messageLabel;
    
    private String nationalId;
    private Gson gson;

    
    public ProfilePanel(String nationalId) {
        this.nationalId = nationalId;
        this.gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create();
        initializeUI();
        loadProfile();
    }

    
    private void initializeUI() {
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: " + BG_DARK + ";");

        
        Label titleLabel = new Label("My Profile");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT + ";"
        );

        Label subtitleLabel = new Label("Manage your account information");
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

        
        VBox personalSection = createSection("👤 Personal Information");
        GridPane personalGrid = new GridPane();
        personalGrid.setHgap(20);
        personalGrid.setVgap(15);

        nationalIdField = new TextField();
        nationalIdField.setPromptText("National ID");
        nationalIdField.setStyle(FIELD_STYLE);
        nationalIdField.setEditable(false);
        nationalIdField.setDisable(true);

        firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        firstNameField.setStyle(FIELD_STYLE);

        middleNameField = new TextField();
        middleNameField.setPromptText("Middle Name");
        middleNameField.setStyle(FIELD_STYLE);

        lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        lastNameField.setStyle(FIELD_STYLE);

        dateOfBirthPicker = new DatePicker();
        dateOfBirthPicker.setPromptText("Date of Birth");
        dateOfBirthPicker.setStyle(FIELD_STYLE);

        genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll(Gender.values());
        genderCombo.setPromptText("Select Gender");
        genderCombo.setStyle(FIELD_STYLE);
        genderCombo.setMaxWidth(Double.MAX_VALUE);

        personalGrid.add(createLabel("National ID"), 0, 0);
        personalGrid.add(nationalIdField, 0, 1);
        personalGrid.add(createLabel("First Name"), 0, 2);
        personalGrid.add(firstNameField, 0, 3);
        personalGrid.add(createLabel("Middle Name"), 0, 4);
        personalGrid.add(middleNameField, 0, 5);
        personalGrid.add(createLabel("Last Name"), 0, 6);
        personalGrid.add(lastNameField, 0, 7);
        personalGrid.add(createLabel("Date of Birth"), 0, 8);
        personalGrid.add(dateOfBirthPicker, 0, 9);
        personalGrid.add(createLabel("Gender"), 0, 10);
        personalGrid.add(genderCombo, 0, 11);

        personalSection.getChildren().add(personalGrid);

        
        VBox contactSection = createSection("📧 Contact Information");
        GridPane contactGrid = new GridPane();
        contactGrid.setHgap(20);
        contactGrid.setVgap(15);

        emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.setStyle(FIELD_STYLE);

        phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setStyle(FIELD_STYLE);

        residenceField = new TextField();
        residenceField.setPromptText("Residence/City");
        residenceField.setStyle(FIELD_STYLE);

        passportField = new TextField();
        passportField.setPromptText("Passport Number (Optional)");
        passportField.setStyle(FIELD_STYLE);

        contactGrid.add(createLabel("Email"), 0, 0);
        contactGrid.add(emailField, 0, 1);
        contactGrid.add(createLabel("Phone Number"), 0, 2);
        contactGrid.add(phoneField, 0, 3);
        contactGrid.add(createLabel("Residence"), 0, 4);
        contactGrid.add(residenceField, 0, 5);
        contactGrid.add(createLabel("Passport Number"), 0, 6);
        contactGrid.add(passportField, 0, 7);

        contactSection.getChildren().add(contactGrid);

        
        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button updateButton = new Button("💾 Update Profile");
        updateButton.setStyle(BTN_PRIMARY);
        updateButton.setOnAction(e -> updateProfile());

        buttonBox.getChildren().add(updateButton);

        
        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(650);

        formCard.getChildren().addAll(
            personalSection,
            createDivider(),
            contactSection,
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
    private void loadProfile() {
        try {
            
            String json = ApiClient.get("/clients/national-id/" + nationalId);
            
            
            Client client = gson.fromJson(json, Client.class);
            
            
            nationalIdField.setText(client.getNationalId().toString());
            firstNameField.setText(client.getFirstName());
            middleNameField.setText(client.getMiddleName());
            lastNameField.setText(client.getLastName());
            dateOfBirthPicker.setValue(client.getDateOfBirth());
            genderCombo.setValue(client.getGender());
            emailField.setText(client.getEmail());
            phoneField.setText(client.getPhoneNumber());
            residenceField.setText(client.getResidence());
            passportField.setText(client.getPassportNumber());
            
            showSuccess("Profile loaded successfully");
            
        } catch (Exception e) {
            showError("Failed to load profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    private void updateProfile() {
        try {
            
            Client client = new Client();
            client.setNationalId(nationalId.toString());
            client.setFirstName(firstNameField.getText().trim());
            client.setMiddleName(middleNameField.getText().trim());
            client.setLastName(lastNameField.getText().trim());
            client.setDateOfBirth(dateOfBirthPicker.getValue());
            client.setGender(genderCombo.getValue());
            client.setEmail(emailField.getText().trim());
            client.setPhoneNumber(phoneField.getText().trim());
            client.setResidence(residenceField.getText().trim());
            client.setPassportNumber(passportField.getText().trim());

        
            String json = gson.toJson(client);

            
            ApiClient.put("/clients/" + nationalId, json);

            
            SessionManager.setCurrentEmail(client.getEmail());

            DialogUtil.showSuccess(
                "Success",
                "Profile Updated",
                "Your profile has been updated successfully!"
            );

            showSuccess("Profile updated successfully");

        } catch (Exception e) {
            showError("Failed to update profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setTextFill(Color.web("#ef4444"));
    }

    
    private void showSuccess(String message) {
        messageLabel.setText("✅ " + message);
        messageLabel.setTextFill(Color.web("#22c55e"));
    }
}