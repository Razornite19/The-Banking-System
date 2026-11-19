package bw.bank.view;

import bw.bank.controller.StaffController;
import bw.bank.model.Account;
import bw.bank.model.BankStaff;
import bw.bank.model.Customer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StaffDashboardView {
    private final Stage stage;
    private final BankStaff staff;
    private final StaffController controller = new StaffController();

    public StaffDashboardView(Stage stage, BankStaff staff) {
        this.stage = stage;
        this.staff = staff;
    }

    public void show() {
        Label header = new Label("Staff Portal — " + staff.getUsername() + " [" + staff.getRole() + "]");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> new LoginView(stage).show());

        Button interestBtn = new Button("Run Monthly Interest Batch");
        interestBtn.setStyle("-fx-background-color: #065F46; -fx-text-fill: white;");
        interestBtn.setOnAction(e -> {
            try {
                int count = controller.runMonthlyInterestBatch();
                showAlert(Alert.AlertType.INFORMATION, "Interest Processed", "Monthly interest credited to " + count + " accounts.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Batch Error", ex.getMessage());
            }
        });

        HBox topBar = new HBox(15, header, interestBtn, logoutBtn);
        topBar.setPadding(new Insets(12));

        TabPane tabs = new TabPane();
        tabs.getTabs().addAll(buildRegisterTab(), buildOpenAccountTab(), buildSearchTab());
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabs);

        stage.setScene(new Scene(root, 840, 600));
    }

    private Tab buildRegisterTab() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField fName = new TextField();
        TextField lName = new TextField();
        TextField natId = new TextField();
        TextField phone = new TextField();
        TextField addr = new TextField();
        TextField user = new TextField();
        PasswordField pass = new PasswordField();
        CheckBox employedCheck = new CheckBox("Customer is Employed (Required for Cheque Account)");
        TextField empName = new TextField();
        TextField empAddr = new TextField();

        grid.addRow(0, new Label("First Name:"), fName);
        grid.addRow(1, new Label("Surname:"), lName);
        grid.addRow(2, new Label("National ID / Passport:"), natId);
        grid.addRow(3, new Label("Phone:"), phone);
        grid.addRow(4, new Label("Address:"), addr);
        grid.addRow(5, new Label("Username:"), user);
        grid.addRow(6, new Label("Initial Password:"), pass);
        grid.add(employedCheck, 1, 7);
        grid.addRow(8, new Label("Employer Name:"), empName);
        grid.addRow(9, new Label("Employer Address:"), empAddr);

        Button saveBtn = new Button("Register Customer");
        saveBtn.setOnAction(e -> {
            try {
                Customer c = controller.registerCustomer(
                    fName.getText(), lName.getText(), natId.getText(), phone.getText(), addr.getText(),
                    user.getText(), pass.getText(), employedCheck.isSelected(), empName.getText(), empAddr.getText()
                );
                showAlert(Alert.AlertType.INFORMATION, "Registered", "Created Customer ID: " + c.getCustomerId());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Registration Error", ex.getMessage());
            }
        });
        grid.add(saveBtn, 1, 10);

        return new Tab("Register Customer", grid);
    }

    private Tab buildOpenAccountTab() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField custIdField = new TextField();
        custIdField.setPromptText("e.g., CUST-001");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("SAVINGS", "INVESTMENT", "CHEQUE");
        typeBox.setValue("SAVINGS");

        TextField depositField = new TextField("0.00");
        TextField branchField = new TextField("Gaborone Main");

        grid.addRow(0, new Label("Customer ID:"), custIdField);
        grid.addRow(1, new Label("Account Type:"), typeBox);
        grid.addRow(2, new Label("Initial Deposit (BWP):"), depositField);
        grid.addRow(3, new Label("Branch:"), branchField);

        Button openBtn = new Button("Open Account");
        openBtn.setOnAction(e -> {
            try {
                Account acc = controller.openAccount(
                    custIdField.getText(), typeBox.getValue(), depositField.getText(), branchField.getText()
                );
                showAlert(Alert.AlertType.INFORMATION, "Account Opened", "New Account Number: " + acc.getAccountNumber());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Account Creation Error", ex.getMessage());
            }
        });
        grid.add(openBtn, 1, 4);

        return new Tab("Open Account", grid);
    }

    private Tab buildSearchTab() {
        TextField searchField = new TextField();
        searchField.setPromptText("Search by Customer ID, National ID, or Name");
        Button searchBtn = new Button("Search");

        TableView<Customer> table = new TableView<>();
        TableColumn<Customer, String> idCol = new TableColumn<>("Customer ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCustomerId()));

        TableColumn<Customer, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));

        TableColumn<Customer, String> natCol = new TableColumn<>("National ID");
        natCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNationalIdOrPassport()));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhoneNumber()));

        table.getColumns().addAll(idCol, nameCol, natCol, phoneCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        searchBtn.setOnAction(e -> {
            try {
                table.setItems(FXCollections.observableArrayList(controller.searchCustomers(searchField.getText())));
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Search Error", ex.getMessage());
            }
        });

        HBox bar = new HBox(10, searchField, searchBtn);
        VBox box = new VBox(10, bar, table);
        box.setPadding(new Insets(15));

        return new Tab("Search Customers", box);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}