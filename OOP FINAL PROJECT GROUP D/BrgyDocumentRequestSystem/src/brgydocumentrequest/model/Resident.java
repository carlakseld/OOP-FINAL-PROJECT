package brgydocumentrequest.model;

import java.time.LocalDate;

public class Resident {
    private String fullName;
    private String address;
    private String contactNumber;
    private String gender;
    private int yearOfBirth;

    // Constructor
    public Resident(String fullName, String address, String contactNumber, String gender, int yearOfBirth) {
        setFullName(fullName);
        setAddress(address);
        setContactNumber(contactNumber);
        setGender(gender);
        setYearOfBirth(yearOfBirth);
    }

    // Getters
    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getGender() {
        return gender;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    // Setter para sa Name: not blank, no digits, no special char
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be blank.");
        }
        String name = fullName.trim();
        if (!name.matches("[a-zA-Z\\s-]+")) {
            throw new IllegalArgumentException(
                    "Full name can only contain letters, spaces, and hyphens."
            );
        }

        this.fullName = name;
    }

    // Setter ADDRESS
    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be blank.");
        }
        if (address.contains("|")) {
            throw new IllegalArgumentException("Address cannot contain '|'.");
        }
        this.address = address.trim();
    }

    // Setter CONTACT NO
    public void setContactNumber(String contactNumber) {
        if (contactNumber == null || !contactNumber.trim().matches("09\\d{9}")) {
            throw new IllegalArgumentException("Contact number must be 11 digits and start with 09.");
        }
        this.contactNumber = contactNumber.trim();
    }

    // Setter GENDER
    public void setGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be blank.");
        }
        String g = gender.trim();
        if (!g.equalsIgnoreCase("Male") && !g.equalsIgnoreCase("Female") && !g.equalsIgnoreCase("Other")) {
            throw new IllegalArgumentException("Gender must be Male, Female, or Other.");
        }
        this.gender = g;
    }

    // Setter  YEAR OF BIRTH
    public void setYearOfBirth(int yearOfBirth) {
        int currentYear = LocalDate.now().getYear();
        if (yearOfBirth < 1900 || yearOfBirth > currentYear) {
            throw new IllegalArgumentException("Year of birth must be between 1900 and " + currentYear + ".");
        }
        this.yearOfBirth = yearOfBirth;
    }
}