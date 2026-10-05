package com.CapitalCommute.commute.UI;

import java.time.LocalDate;
import java.util.regex.Pattern;

import com.CapitalCommute.commute.DTO.LoginResponse;
import com.CapitalCommute.commute.DTO.RegisterRequest;
import com.CapitalCommute.commute.client.ApiClient;
import com.CapitalCommute.commute.model.enums.Gender;
import com.CapitalCommute.commute.util.CaptchaGenerator;
import com.CapitalCommute.commute.util.DialogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegistrationPanel extends VBox {
    
    private TextField nationalIdField;
    private TextField firstNameField;
    private TextField middleNameField;
    private TextField lastNameField;
    private TextField userNameField;
    private DatePicker dateOfBirthPicker;
    private TextField residenceField;
    private TextField passportField;
    private TextField emailField;
    private TextField phoneField;
    private ComboBox<Gender> genderCombo;
    private ComboBox<String> roleCombo;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Label messageLabel;
    private Gson gson;
    private Stage primaryStage;
    
    private CaptchaGenerator captchaGenerator = new CaptchaGenerator();
    private Canvas captchaCanvas;
    private TextField captchaInputField;
    private VBox captchaSection;
    private boolean captchaVerified = false;
    private Button registerButton;
    
    private Label ageLabel;
    private int calculatedAge;
   
    private static final String BG_DARK      = "#0f172a";
    private static final String BG_SURFACE   = "#1e293b";
    private static final String BG_SURFACE2  = "#334155";
    private static final String ACCENT       = "#3b82f6";
    private static final String ACCENT_HOVER = "#2563eb";
    private static final String SUCCESS      = "#22c55e";
    private static final String TEXT         = "#f1f5f9";
    private static final String TEXT_MUTED   = "#94a3b8";
    private static final String BORDER       = "#475569";
    private static final String ERROR        = "#ef4444";

    private static final String FIELD_STYLE =
        "-fx-background-color: " + BG_SURFACE2 + ";" +
        "-fx-border-color: " + BORDER + ";" +
        "-fx-border-radius: 8;" +
        "-fx-background-radius: 8;" +
        "-fx-text-fill: " + TEXT + ";" +
        "-fx-font-size: 13px;" +
        "-fx-padding: 10 14 10 14;" +
        "-fx-pref-width: 280px;" +
        "-fx-prompt-text-fill: #64748b;";

    private static final String BTN_PRIMARY =
        "-fx-background-color: " + ACCENT + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 14px;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 8;" +
        "-fx-padding: 12 32 12 32;" +
        "-fx-cursor: hand;" +
        "-fx-pref-width: 300px;";

    private static final String BTN_PRIMARY_HOVER =
        "-fx-background-color: " + ACCENT_HOVER + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 14px;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 8;" +
        "-fx-padding: 12 32 12 32;" +
        "-fx-cursor: hand;" +
        "-fx-pref-width: 300px;";

    private static final String BTN_SECONDARY =
        "-fx-background-color: " + BG_SURFACE2 + ";" +
        "-fx-text-fill: " + TEXT + ";" +
        "-fx-font-size: 12px;" +
        "-fx-background-radius: 6;" +
        "-fx-padding: 6 12 6 12;" +
        "-fx-cursor: hand;";

    private static final String LABEL_STYLE =
        "-fx-font-size: 13px;" +
        "-fx-text-fill: " + TEXT_MUTED + ";" +
        "-fx-font-weight: 500;";

    private static final String COMBO_STYLE =
        "-fx-background-color: " + BG_SURFACE2 + ";" +
        "-fx-border-color: " + BORDER + ";" +
        "-fx-border-radius: 8;" +
        "-fx-background-radius: 8;" +
        "-fx-text-fill: " + TEXT + ";" +
        "-fx-font-size: 13px;" +
        "-fx-pref-width: 280px;";
    
    private static final Pattern NAME_PATTERN =
        Pattern.compile("^[a-zA-Z\\s]+$");
    private static final Pattern NATIONAL_ID_PATTERN =
        Pattern.compile("^[0-9]{7,10}$");
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[0-9]{10}$");
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern RESIDENCE_PATTERN =
        Pattern.compile("^[a-zA-Z\\s,]+$");
    
    public RegistrationPanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString()))
            .create();
        
        initializeUI();
        setupAgeCalculation();
    }
    
    private void initializeUI() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(20);
        this.setPadding(new Insets(30));
        this.setStyle("-fx-background-color: " + BG_DARK + ";");

        Label titleLabel = new Label("CREATE YOUR ACCOUNT");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + ACCENT + ";"
        );

        Label subtitleLabel = new Label("Join Capital Commute today");
        subtitleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );

        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(32));
        card.setMaxWidth(700);
        card.setStyle(
            "-fx-background-color: " + BG_SURFACE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BG_SURFACE2 + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 8);"
        );

        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(12);
        formGrid.setAlignment(Pos.CENTER);

        initializeFormFields();
        addValidationListeners();

        int row = 0;
        formGrid.add(createLabel("National ID *"),       0, row); formGrid.add(nationalIdField,      1, row++);
        formGrid.add(createLabel("First Name *"),        0, row); formGrid.add(firstNameField,        1, row++);
        formGrid.add(createLabel("Middle Name"),         0, row); formGrid.add(middleNameField,       1, row++);
        formGrid.add(createLabel("Last Name *"),         0, row); formGrid.add(lastNameField,         1, row++);
        formGrid.add(createLabel("User Name *"),         0, row); formGrid.add(userNameField,         1, row++);
        formGrid.add(createLabel("Date of Birth *"),     0, row); formGrid.add(dateOfBirthPicker,     1, row++);
        
        formGrid.add(createLabel("Age"),                 0, row);
        ageLabel = new Label("Age: -- years");
        ageLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        formGrid.add(ageLabel, 1, row++);
        
        formGrid.add(createLabel("Residence *"),         0, row); formGrid.add(residenceField,        1, row++);
        formGrid.add(createLabel("Passport Number"),     0, row); formGrid.add(passportField,         1, row++);
        formGrid.add(createLabel("Email *"),             0, row); formGrid.add(emailField,            1, row++);
        formGrid.add(createLabel("Phone Number *"),      0, row); formGrid.add(phoneField,            1, row++);
        formGrid.add(createLabel("Gender *"),            0, row); formGrid.add(genderCombo,           1, row++);
        formGrid.add(createLabel("Role *"),              0, row); formGrid.add(roleCombo,             1, row++);
        formGrid.add(createLabel("Password *"),          0, row); formGrid.add(passwordField,         1, row++);
        formGrid.add(createLabel("Confirm Password *"),  0, row); formGrid.add(confirmPasswordField,  1, row++);
        
        addCaptchaSection(formGrid, row);
        
        ScrollPane scrollPane = new ScrollPane(formGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;"
        );
        scrollPane.setMaxHeight(550);

        registerButton = new Button("Create Account");
        registerButton.setStyle(BTN_PRIMARY);
        registerButton.setOnMouseEntered(e -> registerButton.setStyle(BTN_PRIMARY_HOVER));
        registerButton.setOnMouseExited(e -> registerButton.setStyle(BTN_PRIMARY));
        registerButton.setOnAction(e -> handleRegister());

        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(500);
        messageLabel.setAlignment(Pos.CENTER);

        Hyperlink loginLink = new Hyperlink("Already have an account? Login here");
        loginLink.setStyle(
            "-fx-text-fill: " + ACCENT + ";" +
            "-fx-font-size: 13px;" +
            "-fx-border-color: transparent;"
        );
        loginLink.setOnAction(e -> showLoginPanel());

        card.getChildren().addAll(
            scrollPane,
            registerButton,
            messageLabel,
            loginLink
        );

        this.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            card
        );
    }
    
    private void initializeFormFields() {
        nationalIdField = new TextField();
        nationalIdField.setPromptText("National ID (7-10 digits)");
        nationalIdField.setStyle(FIELD_STYLE);

        firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        firstNameField.setStyle(FIELD_STYLE);

        middleNameField = new TextField();
        middleNameField.setPromptText("Middle Name (Optional)");
        middleNameField.setStyle(FIELD_STYLE);

        lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        lastNameField.setStyle(FIELD_STYLE);

        userNameField = new TextField();
        userNameField.setPromptText("User Name");
        userNameField.setStyle(FIELD_STYLE);

        dateOfBirthPicker = new DatePicker();
        dateOfBirthPicker.setPromptText("Date of Birth");
        dateOfBirthPicker.setStyle(FIELD_STYLE);

        residenceField = new TextField();
        residenceField.setPromptText("Residence/City");
        residenceField.setStyle(FIELD_STYLE);

        passportField = new TextField();
        passportField.setPromptText("Passport (Optional)");
        passportField.setStyle(FIELD_STYLE);

        emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.setStyle(FIELD_STYLE);

        phoneField = new TextField();
        phoneField.setPromptText("Phone (10 digits)");
        phoneField.setStyle(FIELD_STYLE);

        genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll(Gender.values());
        genderCombo.setPromptText("Select Gender");
        genderCombo.setStyle(COMBO_STYLE);
        genderCombo.setMaxWidth(Double.MAX_VALUE);

        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("CLIENT", "DRIVER");
        roleCombo.setPromptText("Register as...");
        roleCombo.setStyle(COMBO_STYLE);
        roleCombo.setMaxWidth(Double.MAX_VALUE);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password (min 6 characters)");
        passwordField.setStyle(FIELD_STYLE);

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setStyle(FIELD_STYLE);
    }
    
    private void addCaptchaSection(GridPane formGrid, int row) {
        captchaCanvas = captchaGenerator.generate();
        Button refreshBtn = new Button("↻ New Image");
        refreshBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: #3b82f6;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-text-fill: #3b82f6;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 6 12 6 12;"
        );

        HBox captchaImageBox = new HBox(10);
        captchaImageBox.setAlignment(Pos.CENTER_LEFT);
        captchaImageBox.getChildren().addAll(captchaCanvas, refreshBtn);

        captchaInputField = new TextField();
        captchaInputField.setPromptText("Type the characters above");
        captchaInputField.setStyle(FIELD_STYLE);

        Label verifiedLabel = new Label("✓ Security check passed!");
        verifiedLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 13px;");
        verifiedLabel.setVisible(false);

        refreshBtn.setOnAction(e -> {
            captchaCanvas = captchaGenerator.generate();
            captchaImageBox.getChildren().set(0, captchaCanvas);
            captchaInputField.clear();
        });

        captchaSection = new VBox(8);
        captchaSection.getChildren().addAll(captchaImageBox, captchaInputField);

        formGrid.add(createLabel("Security Check *"), 0, row);
        formGrid.add(captchaSection, 1, row++);
        formGrid.add(new Label(""), 0, row);
        formGrid.add(verifiedLabel, 1, row++);
    }
    
    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle(LABEL_STYLE);
        return label;
    }
    
    private void addValidationListeners() {
        nationalIdField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("[0-9]*")) {
                nationalIdField.setText(old);
            }
        });
        
        firstNameField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("[a-zA-Z\\s]*")) {
                firstNameField.setText(old);
            }
        });
        
        middleNameField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("[a-zA-Z\\s]*")) {
                middleNameField.setText(old);
            }
        });
        
        lastNameField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("[a-zA-Z\\s]*")) {
                lastNameField.setText(old);
            }
        });
        
        phoneField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("[0-9]*")) {
                phoneField.setText(old);
            }
        });
        
        residenceField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("[a-zA-Z\\s,]*")) {
                residenceField.setText(old);
            }
        });
    }
    
    private void setupAgeCalculation() {
        dateOfBirthPicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                if (newDate.isAfter(LocalDate.now())) {
                    showError("Birth date cannot be in the future");
                    dateOfBirthPicker.setValue(null);
                    ageLabel.setText("Age: -- years");
                    ageLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
                    return;
                }
                
                if (newDate.isBefore(LocalDate.now().minusYears(120))) {
                    showError("Please enter a valid birth date (age must be 120 years or less)");
                    dateOfBirthPicker.setValue(null);
                    ageLabel.setText("Age: -- years");
                    ageLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
                    return;
                }
                
                calculatedAge = calculateAge(newDate);
                
                if (calculatedAge < 0) {
                    showError("Invalid date of birth");
                    dateOfBirthPicker.setValue(null);
                    ageLabel.setText("Age: -- years");
                    ageLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
                } else {
                    ageLabel.setText("Age: " + calculatedAge + " years");
                    
                    if (calculatedAge < 18) {
                        ageLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                        showError("Note: You are registering as a minor (under 18 years old)");
                    } else {
                        ageLabel.setStyle("-fx-text-fill: #22c55e;");
                    }
                }
            } else {
                ageLabel.setText("Age: -- years");
                ageLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
            }
        });
    }
    
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }
        
        LocalDate today = LocalDate.now();
        
        if (birthDate.isAfter(today)) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        
        if (birthDate.isBefore(today.minusYears(120))) {
            throw new IllegalArgumentException("Birth date is too far in the past (more than 120 years)");
        }
        
        int age = today.getYear() - birthDate.getYear();
        
        if (birthDate.getMonthValue() > today.getMonthValue() || 
            (birthDate.getMonthValue() == today.getMonthValue() && 
             birthDate.getDayOfMonth() > today.getDayOfMonth())) {
            age--;
        }
        
        if (age < 0) {
            throw new IllegalArgumentException("Invalid age calculation");
        }
        
        return age;
    }
    
    private boolean verifyCaptcha() {
        String answer = captchaInputField.getText().trim();
        if (answer.isEmpty()) {
            showError("Please complete the security verification");
            return false;
        }
        if (captchaGenerator.verify(answer)) {
            captchaVerified = true;
            captchaSection.setVisible(false);
            captchaSection.setManaged(false);
            return true;
        } else {
            Canvas newCanvas = captchaGenerator.generate();
            HBox imageBox = (HBox) captchaSection.getChildren().get(0);
            imageBox.getChildren().set(0, newCanvas);
            captchaInputField.clear();
            showError("Incorrect code — new image generated, try again");
            
            return false;
        }
    }
    
    private void handleRegister() {
        if (!validateInputs()) return;
        if (!verifyCaptcha()) return;

        registerButton.setDisable(true);
        showError("Processing registration...");
        
        new Thread(() -> {
            try {
                LocalDate dob = dateOfBirthPicker.getValue();
                boolean isMinor = LocalDate.now().getYear() - dob.getYear() < 18;

                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setNationalId(nationalIdField.getText().trim());
                registerRequest.setFirstName(firstNameField.getText().trim());
                registerRequest.setMiddleName(middleNameField.getText().trim());
                registerRequest.setUserName(userNameField.getText().trim());
                registerRequest.setLastName(lastNameField.getText().trim());
                registerRequest.setDateOfBirth(dob);
                registerRequest.setResidence(residenceField.getText().trim());
                registerRequest.setPassportNumber(passportField.getText().trim());
                registerRequest.setIsMinor(isMinor);
                registerRequest.setEmail(emailField.getText().trim());
                registerRequest.setPhoneNumber(phoneField.getText().trim());
                registerRequest.setGender(genderCombo.getValue());
                registerRequest.setPassword(passwordField.getText());
                registerRequest.setConfirmPassword(confirmPasswordField.getText());
                registerRequest.setRole(roleCombo.getValue());
                registerRequest.setCaptchaSessionId("captcha");
                registerRequest.setCaptchaAnswer(0);

                String requestJson = gson.toJson(registerRequest);
                System.out.println("SENDING: " + requestJson);
                String responseJson = ApiClient.post("/auth/register", requestJson);
                System.out.println("RAW RESPONSE: " + responseJson);
                LoginResponse loginResponse = gson.fromJson(responseJson, LoginResponse.class);

                javafx.application.Platform.runLater(() -> {
                    registerButton.setDisable(false);
                    if (loginResponse != null && loginResponse.getRole() != null) {
                        DialogUtil.showSuccess(
                            "Registration Successful",
                            "Welcome to Capital Commute!",
                            "Your account has been created. Please log in to continue."
                        );
                        showLoginPanel();
                    } else {
                        String msg = (loginResponse != null && loginResponse.getMessage() != null)
                            ? loginResponse.getMessage()
                            : "Registration failed. Please try again.";
                        showError(msg);
                        resetCaptcha();
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    registerButton.setDisable(false);
                    showError("Registration failed: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
                    resetCaptcha();
                    e.printStackTrace();
                });
            }
        }).start();
    }
    
    private void resetCaptcha() {
        captchaVerified = false;
        if (captchaSection != null) {
            captchaSection.setVisible(true);
            captchaSection.setManaged(true);
        }
        if (captchaInputField != null) {
            captchaInputField.clear();
        }
        if (captchaGenerator != null) {
            captchaCanvas = captchaGenerator.generate();
            if (captchaSection != null && captchaSection.getChildren().size() > 0) {
                HBox imageBox = (HBox) captchaSection.getChildren().get(0);
                imageBox.getChildren().set(0, captchaCanvas);
            }
        }
    }
    
    private boolean validateInputs() {
        String nationalId = nationalIdField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String residence = residenceField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (nationalId.isEmpty()) {
            showError("National ID is required");
            return false;
        }
        if (!NATIONAL_ID_PATTERN.matcher(nationalId).matches()) {
            showError("National ID must be 7-10 digits");
            return false;
        }
        
        if (firstName.isEmpty()) {
            showError("First name is required");
            return false;
        }
        if (!NAME_PATTERN.matcher(firstName).matches()) {
            showError("First name can only contain letters");
            return false;
        }
        
        if (lastName.isEmpty()) {
            showError("Last name is required");
            return false;
        }
        if (!NAME_PATTERN.matcher(lastName).matches()) {
            showError("Last name can only contain letters");
            return false;
        }
        
        String middleName = middleNameField.getText().trim();
        if (!middleName.isEmpty() && !NAME_PATTERN.matcher(middleName).matches()) {
            showError("Middle name can only contain letters");
            return false;
        }
        
        if (dateOfBirthPicker.getValue() == null) {
            showError("Date of birth is required");
            return false;
        }
        
        try {
            calculateAge(dateOfBirthPicker.getValue());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return false;
        }
        
        if (residence.isEmpty()) {
            showError("Residence is required");
            return false;
        }
        if (!RESIDENCE_PATTERN.matcher(residence).matches()) {
            showError("Residence can only contain letters, spaces, and commas");
            return false;
        }
        
        if (email.isEmpty()) {
            showError("Email is required");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError("Invalid email format");
            return false;
        }
        
        if (phone.isEmpty()) {
            showError("Phone number is required");
            return false;
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            showError("Phone number must be exactly 10 digits");
            return false;
        }
        
        if (genderCombo.getValue() == null) {
            showError("Please select gender");
            return false;
        }
        
        if (roleCombo.getValue() == null) {
            showError("Please select a role");
            return false;
        }
        
        if (!isStrongPassword(password)) {
            showError("Password must be strong: minimum 8 characters, contain uppercase, lowercase, number, and special character");
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return false;
        }
        
        return true;
    }

    private boolean isStrongPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (specialChars.indexOf(c) != -1) {
                hasSpecialChar = true;
            }
        }
        
        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }
    
    private void showLoginPanel() {
        LoginPanel loginPanel = new LoginPanel(primaryStage);
        Scene scene = new Scene(loginPanel, 800, 600);
        primaryStage.setScene(scene);
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle(
            "-fx-text-fill: " + ERROR + ";" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 500;"
        );
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle(
            "-fx-text-fill: " + SUCCESS + ";" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 500;"
        );
    }
}