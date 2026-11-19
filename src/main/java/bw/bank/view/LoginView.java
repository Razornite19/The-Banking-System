package bw.bank.view;

import bw.bank.controller.LoginController;
import bw.bank.model.BankStaff;
import bw.bank.model.Customer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {
    private final Stage stage;
    private final LoginController loginController = new LoginController();

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Label titleLabel = new Label("Integrated Banking System");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Customer", "Bank Staff");
        roleBox.setValue("Customer");
        roleBox.setMaxWidth(Double.MAX_VALUE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username (e.g., thabo.k or admin)");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (e.g., password123)");

        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #1E3A8A; -fx-text-fill: white; -fx-font-weight: bold;");

        loginBtn.setOnAction(e -> {
            try {
                String role = roleBox.getValue();
                if ("Customer".equals(role)) {
                    Customer customer = loginController.loginCustomer(usernameField.getText(), passwordField.getText());
                    new CustomerDashboardView(stage, customer).show();
                } else {
                    BankStaff staff = loginController.loginStaff(usernameField.getText(), passwordField.getText());
                    new StaffDashboardView(stage, staff).show();
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", ex.getMessage());
            }
        });

        VBox root = new VBox(12, titleLabel, new Label("Login As:"), roleBox, usernameField, passwordField, loginBtn);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 400, 340));
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}