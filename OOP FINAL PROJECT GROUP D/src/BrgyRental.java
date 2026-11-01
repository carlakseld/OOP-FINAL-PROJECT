import java.time.LocalDate;
import java.util.List;

public abstract class BrgyRental implements Payable {
    private static int counter = 1;
    private String rentalId;
    private Resident resident;
    private LocalDate rentalDate;

    // Constructor para sa bagong rental (New Request)
    public BrgyRental(Resident resident, LocalDate rentalDate) {
        setResident(resident);
        setRentalDate(rentalDate);
        this.rentalId = generateId();
    }

    // Loaded Record
    public BrgyRental(String rentalId, Resident resident, LocalDate rentalDate) {
        setRentalId(rentalId);
        setResident(resident);
        if (rentalDate == null) {
            throw new IllegalArgumentException("Rental date cannot be missing.");
        }
        this.rentalDate = rentalDate;   // loaded records may be in the past
    }

    public static void setNextId(int id) {
        if (id < 1) throw new IllegalArgumentException("Next ID must be at least 1.");
        counter = id;
    }
    private String generateId() {
        String id = String.format("RNT-%04d", counter);
        counter++;
        return id;
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        if (rentalId == null || !rentalId.matches("RNT-\\d{4}")) {
            throw new IllegalArgumentException("Invalid Rental ID format. Must be RNT-XXXX.");
        }
        this.rentalId = rentalId;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        if (resident == null) {
            throw new IllegalArgumentException("Resident cannot be null.");
        }
        this.resident = resident;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(LocalDate rentalDate) {
        if (rentalDate == null || rentalDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Rental date cannot be in the past.");
        }
        this.rentalDate = rentalDate;
    }

    public abstract String getRentalType();
    public abstract boolean checkAvailability(List<BrgyRental> bookedList);
}