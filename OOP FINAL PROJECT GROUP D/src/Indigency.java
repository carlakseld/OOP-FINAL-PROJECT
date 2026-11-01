import java.time.LocalDate;

public class Indigency extends DocumentRequest {
    private static final double INCOME_LIMIT = 10000.0;
    private double monthlyIncome;
    private String reason;
    private String supportingDocument;

    // Constructor para sa bagong Indigency Record
    public Indigency(Resident resident, double monthlyIncome, String reason, String supportingDocument) {
        super(resident);
        setMonthlyIncome(monthlyIncome);
        setReason(reason);
        setSupportingDocument(supportingDocument);
    }

    // Overloading Constructor para sa DataBase
    public Indigency(Resident resident, String requestId, LocalDate dateRequested, double monthlyIncome, String reason, String supportingDocument) {
        super(resident, requestId, dateRequested);
        setMonthlyIncome(monthlyIncome);
        setReason(reason);
        setSupportingDocument(supportingDocument);
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public String getReason() {
        return reason;
    }

    public String getSupportingDocument() {
        return supportingDocument;
    }

    // Setter para sa monthly income
    public void setMonthlyIncome(double monthlyIncome) {
        if (Double.isNaN(monthlyIncome) || Double.isInfinite(monthlyIncome)) {
            throw new IllegalArgumentException("Monthly income must be a valid number.");
        }
        if (monthlyIncome > INCOME_LIMIT) {
            throw new IllegalArgumentException("Monthly income must be below 10,000.");
        }
        if (monthlyIncome < 0) {
            throw new IllegalArgumentException("Monthly income cannot be negative.");
        }
        this.monthlyIncome = monthlyIncome;
    }

    // Setter para sa reason
    public void setReason(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be blank.");
        }
        this.reason = reason;
    }

    // Setter para sa supporting document
    public void setSupportingDocument(String supportingDocument) {
        if (supportingDocument == null || supportingDocument.trim().isEmpty()) {
            throw new IllegalArgumentException("Supporting document cannot be blank.");
        }
        this.supportingDocument = supportingDocument.trim();
    }

    @Override
    public String getDocumentType() {
        return "Indigency";
    }

    @Override
    public boolean isEligible() {
        return monthlyIncome <= INCOME_LIMIT;
    }
    @Override
    public String getSummary() {
        return super.getSummary()
                + "\nType: " + getDocumentType()
                + "\nMonthly Income: " + String.format("%.2f", monthlyIncome)
                + "\nReason: " + reason
                + "\nSupporting Document: " + supportingDocument
                + "\nEligible: " + (isEligible() ? "Yes" : "No")
                + "\nFee: FREE";
    }

}