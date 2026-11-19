package bw.bank;

import bw.bank.controller.CustomerController;
import bw.bank.controller.LoginController;
import bw.bank.controller.StaffController;
import bw.bank.dao.DatabaseConnection;
import bw.bank.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {
    private LoginController loginController;
    private CustomerController customerController;
    private StaffController staffController;

    @BeforeEach
    void setupFreshDatabase() throws Exception {
        Files.deleteIfExists(Path.of("test_banking.db"));
        DatabaseConnection.setDbUrl("jdbc:sqlite:test_banking.db");
        loginController = new LoginController();
        customerController = new CustomerController();
        staffController = new StaffController();
    }

    @Test
    void testFullLoginDepositAndSavingsWithdrawalRestriction() throws Exception {
        // 1. Login seeded customer Thabo (password: password123)
        Customer thabo = loginController.loginCustomer("thabo.k", "password123");
        assertNotNull(thabo);

        // 2. Deposit BWP 500.00 into Savings Account ACC-1001 (initial balance BWP 2500.00)
        customerController.handleDeposit("ACC-1001", "500.00", "Bonus Deposit");
        List<Account> accounts = customerController.loadAccounts(thabo.getCustomerId());
        Account savings = accounts.stream()
                .filter(a -> a.getAccountNumber().equals("ACC-1001"))
                .findFirst()
                .orElseThrow();
        assertEquals(3000.00, savings.getBalance(), 0.01);

        // 3. Attempt withdrawal from Savings Account -> must be strictly rejected (FR 4.3)
        assertThrows(UnsupportedOperationException.class, () ->
            customerController.handleWithdraw("ACC-1001", "200.00")
        );
    }

    @Test
    void testAccountCreationRulesForInvestmentAndCheque() throws Exception {
        // Unemployed customer CUST-003 cannot open a Cheque Account (FR 3.3)
        assertThrows(IllegalArgumentException.class, () ->
            staffController.openAccount("CUST-003", "CHEQUE", "1000.00", "Gaborone Main")
        );

        // Investment Account below BWP 500.00 must be rejected (FR 3.2)
        assertThrows(IllegalArgumentException.class, () ->
            staffController.openAccount("CUST-003", "INVESTMENT", "499.99", "Gaborone Main")
        );

        // Investment Account at BWP 500.00 succeeds
        Account inv = staffController.openAccount("CUST-003", "INVESTMENT", "500.00", "Gaborone Main");
        assertEquals(500.00, inv.getBalance(), 0.01);
    }

    @Test
    void testMonthlyInterestBatchPayout() throws Exception {
        BankStaff admin = loginController.loginStaff("admin", "password123");
        assertNotNull(admin);

        // Run monthly interest batch
        int credited = staffController.runMonthlyInterestBatch();
        assertTrue(credited >= 4);

        // ACC-1002 is an InvestmentAccount with BWP 5000.00 -> +5% = BWP 5250.00
        List<Account> thaboAccounts = staffController.getCustomerAccounts("CUST-001");
        Account inv = thaboAccounts.stream()
                .filter(a -> a.getAccountNumber().equals("ACC-1002"))
                .findFirst()
                .orElseThrow();
        assertEquals(5250.00, inv.getBalance(), 0.01);
    }
}