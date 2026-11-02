package brgydocumentrequest.model;

import java.time.LocalDate;

public class Clearance extends DocumentRequest implements Payable {
    private static final double FEE = 50.0;
    private String purpose;

    // Constructor for a NEW clearance
    public Clearance(Resident resident, String purpose) {
        super (resident);
        setPurpose(purpose);
    }

    // Constructor for clearance DATABASE
    public Clearance(Resident resident, String requestId, LocalDate dateRequested, String purpose) {
        super(resident, requestId, dateRequested);
        setPurpose(purpose);
    }

    // Getter for purpose
    public String getPurpose() {
        return purpose;
    }

    // Setter for purpose: not blank
    public void setPurpose(String purpose) {
        // Reject a missing or blank purpose
        if (purpose == null || purpose.isEmpty()) {
            throw new IllegalArgumentException("Purpose cannot be blank.");
        }
        this.purpose = purpose.trim();
    }

    // Returns the type name
    @Override
    public String getDocumentType() {
        return "Clearance";
    }

    // Eligible when the purpose is valid
    @Override
    public boolean isEligible() {
        return purpose != null && !purpose.trim().isEmpty();
    }

    @Override
    public double getFee() {
        return FEE;
    }

    @Override
    public String getPaymentDisplay() {
        return String.format("₱%.2f", getFee());
    }

    // Summary for the staff record
    @Override
    public String getSummary() {
        return super.getSummary()
                + " | Type: " + getDocumentType()
                + " | Purpose: " + purpose
                + " | Eligible: " + (isEligible() ? "Yes" : "No")
                + " | Fee: PHP " + String.format("%.2f", FEE);
    }
}