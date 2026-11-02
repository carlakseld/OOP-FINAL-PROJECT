package brgydocumentrequest.model;

import java.time.LocalDate;
import java.util.List;

public class TentRental extends BrgyRental {
    private static final int TOTAL_STOCK = 30;
    private static final double TENT_FEE_PER_DAY = 300.0;

    private int quantity;
    private int numberOfDays;

    // Constructor para sa Rent req
    public TentRental(Resident resident, LocalDate rentalDate, int quantity, int numberOfDays) {
        super(resident, rentalDate);
        setQuantity(quantity);
        setNumberOfDays(numberOfDays);
    }

    // OverloadingConstructor para sa DATABASE
    public TentRental(String rentalId, Resident resident, LocalDate rentalDate, int quantity, int numberOfDays) {
        super(rentalId, resident, rentalDate);
        setQuantity(quantity);
        setNumberOfDays(numberOfDays);
    }

    //getters
    public int getQuantity() {
        return quantity;
    }
    public int getNumberOfDays() {
        return numberOfDays;
    }


    public void setQuantity(int quantity) {
        if (quantity <= 0 || quantity > TOTAL_STOCK) {
            throw new IllegalArgumentException("Tent quantity must be between 1 and " + TOTAL_STOCK + ".");
        }
        this.quantity = quantity;
    }

    public void setNumberOfDays(int numberOfDays) {
        if (numberOfDays <= 0) {
            throw new IllegalArgumentException("Number of days must be at least 1.");
        }
        this.numberOfDays = numberOfDays;
    }

    @Override
    public String getRentalType() {
        return "Tent Rental";
    }

    @Override
    public double getFee() {
        return quantity * TENT_FEE_PER_DAY * numberOfDays;
    }

    @Override
    public String getPaymentDisplay() {
        return String.format("PHP %.2f", getFee());
    }

    @Override
    public boolean checkAvailability(List<BrgyRental> bookedList) {
        LocalDate newStart = this.getRentalDate();
        LocalDate newEnd = newStart.plusDays(this.numberOfDays - 1);

        // TENT STOCK LEFT
        for (LocalDate date = newStart; !date.isAfter(newEnd); date = date.plusDays(1)) {
            int tentsUsedOnThisDay = 0;

            for (BrgyRental rental : bookedList) {
                if (rental instanceof TentRental) {
                    TentRental existing = (TentRental) rental;
                    LocalDate existingStart = existing.getRentalDate();
                    LocalDate existingEnd = existingStart.plusDays(existing.numberOfDays - 1);

                    if (!date.isBefore(existingStart) && !date.isAfter(existingEnd)) {
                        tentsUsedOnThisDay += existing.getQuantity();
                    }
                }
            }

            // FALSE if tents booked ay lumagpas sa 30 during that day
            if ((tentsUsedOnThisDay + this.quantity) > TOTAL_STOCK) {
                return false;
            }
        }
        return true;
    }
}