package brgydocumentrequest.model;

import java.time.LocalDate;

public class Residency extends DocumentRequest implements Payable {

    private static final int MIN_MONTHS = 6;
    private static final double FEE = 30.0;
    private int yearsOfResidency;
    private int monthsOfResidency;

    // Constructor for a NEW residency request
    public Residency(Resident resident, int yearsOfResidency, int monthsOfResidency) {
        super(resident);
        setYearsOfResidency(yearsOfResidency);
        setMonthsOfResidency(monthsOfResidency);

        if (!isEligible()) {
            throw new IllegalArgumentException("Resident is not eligible: Total residency must be at least 6 months.");
        }
    }

    // Overloading Constructor for DATABASE
    public Residency(Resident resident, String requestId, LocalDate dateRequested, int yearsOfResidency, int monthsOfResidency) {
        super(resident, requestId, dateRequested);
        setYearsOfResidency(yearsOfResidency);
        setMonthsOfResidency(monthsOfResidency);
    }

    // Getter for years and months of residency
    public int getYearsOfResidency() {
        return yearsOfResidency;
    }
    public int getMonthsOfResidency() {
        return monthsOfResidency;
    }

    // Setter for years of residency: zero or more
    public void setYearsOfResidency(int yearsOfResidency) {
        // Reject negative years
        if (yearsOfResidency < 0) {
            throw new IllegalArgumentException("Years of residency cannot be negative.");
        }
        this.yearsOfResidency = yearsOfResidency;
    }

    // Setter for months of residency: 0 to 11 (12 months is already 1 year)
    public void setMonthsOfResidency(int monthsOfResidency) {
        // Reject months below 0 or above 11
        if (monthsOfResidency < 0 || monthsOfResidency > 11) {
            throw new IllegalArgumentException("Months of residency must be from 0 to 11.");
        }

        this.monthsOfResidency = monthsOfResidency;
    }

    // Converts the years to months and adds the months
    public int getTotalMonths() {
        return (int) yearsOfResidency * 12 + monthsOfResidency;
    }

    // Eligible when the TOTAL months (years converted to months + months) reach the minimum
    @Override
    public boolean isEligible() {
        return getTotalMonths() >= MIN_MONTHS;
    }

    @Override
    public String getDocumentType() {
        return "Residency";
    }

    // Summary for the staff record
    @Override
    public String getSummary() {
        return super.getSummary()
                + "\n Type: " + getDocumentType()
                + "\n Years: " + yearsOfResidency
                + "\n Months: " + monthsOfResidency
                + "\n Eligible: " + (isEligible() ? "Yes" : "No")
                + "\n Fee: PHP " + String.format("%.2f", FEE);
    }

    @Override
    public double getFee() {
        return FEE;
    }

    @Override
    public String getPaymentDisplay() {
        return String.format("₱%.2f", getFee());
    }
}