package bw.bank.view;

import bw.bank.controller.CustomerController;
import bw.bank.model.Account;
import bw.bank.model.Customer;
import bw.bank.model.Transaction;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class CustomerDashboardView {
    private final Stage stage;
    private final Customer customer;
    private final CustomerController controller = new CustomerController();

    private final TableView<Account> accountTable = new TableView<>();
    private final TableView<Transaction> txnTable = new TableView<>();

    public CustomerDashboardView(Stage stage, Customer customer) {
        this.stage = stage;
        this.customer = customer;
    }

    public void show() {
        Label header = new Label("Welcome, " + customer.getFullName() + " (" + customer.getCustomerId() + ")");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> new LoginView(stage).show());

        HBox topBar = new HBox(20, header, logoutBtn);
        topBar.setPadding(new Insets(12));

        setupAccountTable();
        setupTransactionTable();

        accountTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                refreshTransactions(selected.getAccountNumber());
            }
        });

        TextField amountField = new TextField();
        amountField.setPromptText("Amount (BWP)");

        TextField refField = new TextField();
        refField.setPromptText("Reference (optional)");

        Button depositBtn = new Button("Deposit");
        depositBtn.setOnAction(e -> {
            Account selected = accountTable.getSelectionModel().getSelectedItem();
            try {
                String accNum = selected != null ? selected.getAccountNumber() : null;
                controller.handleDeposit(accNum, amountField.getText(), refField.getText());
                amountField.clear();
                refField.clear();
                refreshAccounts();
                refreshTransactions(accNum);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Deposit completed.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Deposit Error", ex.getMessage());
            }
        });

        Button withdrawBtn = new Button("Withdraw");
        withdrawBtn.setOnAction(e -> {
            Account selected = accountTable.getSelectionModel().getSelectedItem();
            try {
                String accNum = selected != null ? selected.getAccountNumber() : null;
                controller.handleWithdraw(accNum, amountField.getText());
                amountField.clear();
                refreshAccounts();
                refreshTransactions(accNum);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Withdrawal completed.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Withdrawal Error", ex.getMessage());
            }
        });

        Button exportBtn = new Button("Export History (CSV)");
        exportBtn.setOnAction(e -> {
            Account selected = accountTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Account Selected", "Select an account to export.");
                return;
            }
            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName(selected.getAccountNumber() + "_transactions.csv");
            File file = chooser.showSaveDialog(stage);
            if (file != null) {
                try {
                    controller.exportTransactionsCSV(selected.getAccountNumber(), file);
                    showAlert(Alert.AlertType.INFORMATION, "Exported", "Saved to " + file.getName());
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Export Failed", ex.getMessage());
                }
            }
        });

        HBox actionBar = new HBox(10, amountField, refField, depositBtn, withdrawBtn, exportBtn);
        actionBar.setPadding(new Insets(10, 0, 10, 0));

        VBox centerBox = new VBox(10, new Label("Your Accounts"), accountTable, actionBar, new Label("Transaction History"), txnTable);
        centerBox.setPadding(new Insets(12));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(centerBox);

        refreshAccounts();
        stage.setScene(new Scene(root, 820, 620));
    }

    private void setupAccountTable() {
        TableColumn<Account, String> numCol = new TableColumn<>("Account Number");
        numCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountNumber()));

        TableColumn<Account, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountType()));

        TableColumn<Account, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBranch()));

        TableColumn<Account, Number> balCol = new TableColumn<>("Balance (BWP)");
        balCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getBalance()));

        accountTable.getColumns().addAll(numCol, typeCol, branchCol, balCol);
        accountTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        accountTable.setPrefHeight(160);
    }

    private void setupTransactionTable() {
        TableColumn<Transaction, String> idCol = new TableColumn<>("Txn ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTransactionId()));

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date/Time");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDateTime().toString()));

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));

        TableColumn<Transaction, Number> amtCol = new TableColumn<>("Amount (BWP)");
        amtCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getAmount()));

        TableColumn<Transaction, Number> afterCol = new TableColumn<>("Balance After");
        afterCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getBalanceAfterTransaction()));

        TableColumn<Transaction, String> refCol = new TableColumn<>("Reference");
        refCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReference()));

        txnTable.getColumns().addAll(idCol, dateCol, typeCol, amtCol, afterCol, refCol);
        txnTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void refreshAccounts() {
        try {
            accountTable.setItems(FXCollections.observableArrayList(controller.loadAccounts(customer.getCustomerId())));
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Load Error", ex.getMessage());
        }
    }

    private void refreshTransactions(String accountNumber) {
        try {
            txnTable.setItems(FXCollections.observableArrayList(controller.loadTransactions(accountNumber)));
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Load Error", ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}