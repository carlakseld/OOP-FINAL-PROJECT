import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * RequestManager: keeps all records in memory and checks the rules.
 * Every change is saved to the database FIRST (through RequestStorage),
 * so a failed save never leaves a "ghost" record in memory.
 */
public class RequestManager {

    // Same stock as TentRental.TOTAL_STOCK (that constant is private there)
    private static final int TENT_STOCK = 30;

    // All request records in memory (Residency, Indigency, Clearance)
    private final List<DocumentRequest> requests;

    // All rental records in memory (Court and Tent together)
    private final List<BrgyRental> rentals;

    // Helper object that talks to the SQLite database
    private final RequestStorage storage;

    // Constructor: creates the storage helper (and the DB tables) and the two empty lists
    public RequestManager() {
        this.storage = new RequestStorage();
        this.requests = new ArrayList<>();
        this.rentals = new ArrayList<>();
    }

    // Reads all saved records into memory and resets both ID counters
    public void loadAll() {
        // Clear first so loading twice never duplicates records
        requests.clear();
        rentals.clear();
        requests.addAll(storage.loadRequests());
        rentals.addAll(storage.loadRentals());

        // Highest request number (REQ-0007 -> 7)
        int highestRequest = 0;
        for (DocumentRequest request : requests) {
            int number = Integer.parseInt(request.getRequestId().substring(4));
            if (number > highestRequest) {
                highestRequest = number;
            }
        }
        // Next new request continues after the highest saved one
        DocumentRequest.setNextId(highestRequest + 1);

        // Highest rental number (RNT-0003 -> 3)
        int highestRental = 0;
        for (BrgyRental rental : rentals) {
            int number = Integer.parseInt(rental.getRentalId().substring(4));
            if (number > highestRental) {
                highestRental = number;
            }
        }
        // Next new rental continues after the highest saved one
        BrgyRental.setNextId(highestRental + 1);
    }

    //  REQUESTS

    // Adds a new request after checking it is valid, eligible, and not a duplicate
    public void addRequest(DocumentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }
        if (!request.isEligible()) {
            throw new IllegalArgumentException(
                    "Request is not eligible: " + request.getDocumentType() + ".");
        }
        for (DocumentRequest existing : requests) {
            if (existing.getRequestId().equals(request.getRequestId())) {
                throw new IllegalArgumentException("Duplicate request ID: " + request.getRequestId());
            }
        }
        // Database first, then memory
        storage.insertRequest(request);
        requests.add(request);
    }

    // Replaces a saved request with an edited one (same ID, same type, same date requested)
    public void updateRequest(DocumentRequest updated) {
        if (updated == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }
        // The request must already exist (findById throws if it does not)
        DocumentRequest existing = findById(updated.getRequestId());
        if (!existing.getDocumentType().equals(updated.getDocumentType())) {
            throw new IllegalArgumentException("The type of a saved request cannot be changed.");
        }
        if (!existing.getDateRequested().equals(updated.getDateRequested())) {
            throw new IllegalArgumentException("The date requested cannot be changed.");
        }
        if (!updated.isEligible()) {
            throw new IllegalArgumentException(
                    "Request is not eligible: " + updated.getDocumentType() + ".");
        }
        // Database first, then memory
        storage.updateRequest(updated);
        requests.set(requests.indexOf(existing), updated);
    }

    // Deletes a saved request for good
    public void deleteRequest(String id) {
        DocumentRequest existing = findById(id);
        storage.deleteRequest(existing.getRequestId());
        requests.remove(existing);
    }

    // Finds one request by its ID (e.g., REQ-0001) or throws if not found
    public DocumentRequest findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Request ID cannot be blank.");
        }
        String cleanId = id.trim().toUpperCase();
        for (DocumentRequest request : requests) {
            if (request.getRequestId().equals(cleanId)) {
                return request;
            }
        }
        throw new IllegalArgumentException("No request found with ID: " + cleanId);
    }

    // ===================== RENTALS =====================

    // Adds a new rental after its own booking rule says it is available
    public void addRental(BrgyRental rental) {
        if (rental == null) {
            throw new IllegalArgumentException("Rental cannot be null.");
        }
        for (BrgyRental existing : rentals) {
            if (existing.getRentalId().equals(rental.getRentalId())) {
                throw new IllegalArgumentException("Duplicate rental ID: " + rental.getRentalId());
            }
        }
        // Each rental type applies its own rule (court: time/whole-day clash; tent: 30-tent stock)
        if (!rental.checkAvailability(rentals)) {
            throw new IllegalArgumentException(conflictMessage(rental));
        }
        // Database first, then memory
        storage.insertRental(rental);
        rentals.add(rental);
    }

    // Replaces a saved rental with an edited one (same ID, same kind: court stays court, tent stays tent)
    public void updateRental(BrgyRental updated) {
        if (updated == null) {
            throw new IllegalArgumentException("Rental cannot be null.");
        }
        // The rental must already exist (findRentalById throws if it does not)
        BrgyRental existing = findRentalById(updated.getRentalId());
        // Compare the class (a court may switch Whole Day <-> Hourly, but never become a tent)
        if (existing.getClass() != updated.getClass()) {
            throw new IllegalArgumentException("The type of a saved rental cannot be changed.");
        }
        // A NEW date must be today or later (an unchanged old date is allowed)
        boolean dateChanged = !existing.getRentalDate().equals(updated.getRentalDate());
        if (dateChanged && updated.getRentalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Rental date must be today or a future date.");
        }
        // Check against every OTHER rental, so the rental never clashes with its own old version
        List<BrgyRental> others = new ArrayList<>(rentals);
        others.remove(existing);
        if (!updated.checkAvailability(others)) {
            throw new IllegalArgumentException(conflictMessage(updated));
        }
        // Database first, then memory
        storage.updateRental(updated);
        rentals.set(rentals.indexOf(existing), updated);
    }

    // Deletes a saved rental for good; its date/time and tents become free again
    public void deleteRental(String id) {
        BrgyRental existing = findRentalById(id);
        storage.deleteRental(existing.getRentalId());
        rentals.remove(existing);
    }

    // Finds one rental by its ID (e.g., RNT-0001) or throws if not found
    public BrgyRental findRentalById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Rental ID cannot be blank.");
        }
        String cleanId = id.trim().toUpperCase();
        for (BrgyRental rental : rentals) {
            if (rental.getRentalId().equals(cleanId)) {
                return rental;
            }
        }
        throw new IllegalArgumentException("No rental found with ID: " + cleanId);
    }

    // Error text shown when checkAvailability() says false
    private String conflictMessage(BrgyRental rental) {
        if (rental instanceof CourtRental) {
            return "Conflict Error: Court has a schedule clash or whole-day conflict on this date.";
        }
        return "Stock Error: Not enough tents available (Exceeds " + TENT_STOCK
                + " limit for the selected dates).";
    }


    // Copy of the request list so outside code cannot change the real list
    public List<DocumentRequest> getAll() {
        return new ArrayList<>(requests);
    }

    // Copy of the rental list so outside code cannot change the real list
    public List<BrgyRental> getAllRentals() {
        return new ArrayList<>(rentals);
    }

    // How many tents are still free on a given date
    public int getTentsAvailable(LocalDate date) {
        int used = 0;
        for (BrgyRental rental : rentals) {
            if (rental instanceof TentRental) {
                TentRental tent = (TentRental) rental;
                LocalDate start = tent.getRentalDate();
                LocalDate end = start.plusDays(tent.getNumberOfDays() - 1);
                if (!date.isBefore(start) && !date.isAfter(end)) {
                    used += tent.getQuantity();
                }
            }
        }
        return TENT_STOCK - used;
    }

    // Show Records: one text entry per saved record (requests first, then rentals)
    public List<String> showRecords() {
        List<String> lines = new ArrayList<>();
        for (DocumentRequest request : requests) {
            lines.add(request.getSummary());
        }
        for (BrgyRental rental : rentals) {
            lines.add(rental.getRentalId() + " | " + rental.getResident().getFullName()
                    + " | " + rental.getRentalType()
                    + " | Date: " + rental.getRentalDate()
                    + " | Fee: " + rental.getPaymentDisplay());
        }
        return lines;
    }
}