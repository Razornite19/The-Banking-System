package bw.bank.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer {
    private String customerId;
    private String firstName;
    private String lastName;
    private String nationalIdOrPassport;
    private String phoneNumber;
    private String address;
    private String username;
    private String passwordHash;
    private final List<Account> accounts;

    public Customer(String customerId, String firstName, String lastName,
                    String nationalIdOrPassport, String phoneNumber, String address,
                    String username, String passwordHash) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalIdOrPassport = nationalIdOrPassport;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.username = username;
        this.passwordHash = passwordHash;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        if (account != null) {
            this.accounts.add(account);
        }
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public String getCustomerId() { return customerId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getNationalIdOrPassport() { return nationalIdOrPassport; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
}