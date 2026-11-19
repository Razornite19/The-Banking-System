package bw.bank.model;

public class EmployedCustomer extends Customer {
    private String employerName;
    private String employerAddress;

    public EmployedCustomer(String customerId, String firstName, String lastName,
                            String nationalIdOrPassport, String phoneNumber, String address,
                            String username, String passwordHash,
                            String employerName, String employerAddress) {
        super(customerId, firstName, lastName, nationalIdOrPassport, phoneNumber, address, username, passwordHash);
        if (employerName == null || employerName.trim().isEmpty() ||
            employerAddress == null || employerAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("EmployedCustomer requires both employer name and employer address.");
        }
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getEmployerAddress() {
        return employerAddress;
    }
}