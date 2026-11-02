package brgydocumentrequest.model;

import java.time.LocalDate;
import java.util.List;

public class CourtRental extends BrgyRental {
    private static final double HOURLY_FEE = 500.0;
    private static final double WHOLE_DAY_FEE = 10000.0;

    private boolean isWholeDay;
    private int startHour;      // Halimbawa: 8 (8 AM) hanggang 21 (9 PM)
    private int durationHours;  // Bilang ng oras para sa hourly rental

    // Constructor para sa NEW REQ
    public CourtRental(Resident resident, LocalDate rentalDate, boolean isWholeDay, int startHour, int durationHours) {
        super(resident, rentalDate);
        this.isWholeDay = isWholeDay;
        if (!isWholeDay) {
            setHourlyDetails(startHour, durationHours);
        }
    }

    // Constructor para sa DATABASE
    public CourtRental(String rentalId, Resident resident, LocalDate rentalDate, boolean isWholeDay, int startHour, int durationHours) {
        super(rentalId, resident, rentalDate);
        this.isWholeDay = isWholeDay;
        if (!isWholeDay) {
            setHourlyDetails(startHour, durationHours);
        }
    }

    private void setHourlyDetails(int startHour, int durationHours) {
        if (startHour < 6 || startHour > 21) {
            throw new IllegalArgumentException("Start hour must be between 6 AM (6) and 9 PM (21).");
        }
        if (durationHours <= 0 || (startHour + durationHours) > 22) {
            throw new IllegalArgumentException("Invalid duration or exceeds court operating hours (up to 10 PM).");
        }
        this.startHour = startHour;
        this.durationHours = durationHours;
    }

    // Returns whether the rental is for the whole day.
    public boolean isWholeDay() {
        return isWholeDay;
    }

    // Returns the starting hour of an hourly rental.
    public int getStartHour() {
        return startHour;
    }

    // Returns the duration of an hourly rental.
    public int getDurationHours() {
        return durationHours;
    }

    @Override
    public String getRentalType() {
        return isWholeDay ? "Covered Court (Whole Day)" : "Covered Court (Hourly)";
    }

    @Override
    public double getFee() {
        return isWholeDay ? WHOLE_DAY_FEE : (HOURLY_FEE * durationHours);
    }

    @Override
    public String getPaymentDisplay() {
        return String.format("PHP %.2f", getFee());
    }

    @Override
    public boolean checkAvailability(List<BrgyRental> bookedList) {
        for (BrgyRental rental : bookedList) {
            if (rental instanceof CourtRental && rental.getRentalDate().equals(this.getRentalDate())) {
                CourtRental existing = (CourtRental) rental;

                // clash if the planned schedule is already exist
                if (existing.isWholeDay || this.isWholeDay) {
                    return false;
                }

                //Time Slot Overlap Check
                int existingEnd = existing.startHour + existing.durationHours;
                int thisEnd = this.startHour + this.durationHours;

                if (this.startHour < existingEnd && existing.startHour < thisEnd) {
                    return false; // May kasabay sa oras na ito
                }
            }
        }
        return true;
    }
}