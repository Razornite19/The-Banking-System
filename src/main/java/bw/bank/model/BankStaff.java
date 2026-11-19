package bw.bank.model;

public class BankStaff {
    private String staffId;
    private String username;
    private String passwordHash;
    private String role; // "ADMIN" or "TELLER"

    public BankStaff(String staffId, String username, String passwordHash, String role) {
        this.staffId = staffId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getStaffId() { return staffId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
}