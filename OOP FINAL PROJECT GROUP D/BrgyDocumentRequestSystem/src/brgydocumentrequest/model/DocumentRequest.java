package brgydocumentrequest.model;

import java.time.LocalDate;

public abstract class DocumentRequest {

    private static int nextId = 1;
    private final String requestId;
    private Resident resident;
    private LocalDate dateRequested;

    // Constructor for a NEW request:
    public DocumentRequest(Resident resident) {
        // Reject a missing resident
        if (resident == null) {
            throw new IllegalArgumentException("Resident cannot be null.");
        }
        this.resident = resident;
        this.dateRequested = LocalDate.now();
        // ID is generated last so a failed check above never uses up a number
        this.requestId = String.format("REQ-%04d", nextId++);
    }

    // Constructor for DATABASE
    public DocumentRequest(Resident resident, String requestId, LocalDate dateRequested) {
        // Reject a missing resident
        if (resident == null) {
            throw new IllegalArgumentException("Resident cannot be null.");
        }
        //invlid id format
        if (requestId == null || !requestId.matches("REQ-\\d{4,}")) {
            throw new IllegalArgumentException("Invalid request ID format. Expected REQ-0001.");
        }
        // Reject a missing date or a date in the future
        if (dateRequested == null || dateRequested.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date requested must not be missing or in the future.");
        }
        this.resident = resident;
        this.requestId = requestId;
        this.dateRequested = dateRequested;
    }

    // Basic summary (ID + resident name) tapos subclasses add their own details
    public String getSummary() {
        return requestId + " | " + getResidentName();
    }

    // Getter
    public String getRequestId() {
        return requestId;
    }
    public Resident getResident() {
        return resident;
    }
    public LocalDate getDateRequested() {
        return dateRequested;
    }
    public String getResidentName() {
        return resident.getFullName();
    }
    public static int getNextId() {
        return nextId;
    }

    // Setter for the ID counter
    public static void setNextId(int id) {
        // Reject a counter below 1
        if (id < 1) {
            throw new IllegalArgumentException("Next ID must be at least 1.");
        }
        nextId = id;
    }

    // Abstract
    public abstract String getDocumentType();
    public abstract boolean isEligible();
}