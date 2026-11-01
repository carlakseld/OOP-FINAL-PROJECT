import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RequestStorage {


   // Database file location
    private static final String DB_FILE =
           "C:/Users/Aksel Dellomas/IdeaProjects/OOP FINAL PROJECT GROUP D/barangay.db";

    // SQLite connection URL
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

    //  DB LOC
    public static String getDatabasePath() {
        return DB_FILE;
    }

    // Opens a connection to the SQLite database
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Builds a Resident from the 5 shared columns of any table
    private Resident readResident(ResultSet rs) throws SQLException {
        return new Resident(
                rs.getString("full_name"),
                rs.getString("address"),
                rs.getString("contact"),
                rs.getString("gender"),
                rs.getInt("year_of_birth"));
    }

    //INSERTS of the 5 Subclasses
    public void insertRequest(DocumentRequest request) {
        Resident r = request.getResident();
        try (Connection conn = connect()) {
            if (request instanceof Clearance) {
                Clearance c = (Clearance) request;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO clearances (request_id, full_name, address, contact, gender, year_of_birth, date_requested, purpose) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setString(1, c.getRequestId());
                    ps.setString(2, r.getFullName());
                    ps.setString(3, r.getAddress());
                    ps.setString(4, r.getContactNumber());
                    ps.setString(5, r.getGender());
                    ps.setInt(6, r.getYearOfBirth());
                    ps.setString(7, c.getDateRequested().toString());
                    ps.setString(8, c.getPurpose());
                    ps.executeUpdate();
                }
            } else if (request instanceof Indigency) {
                Indigency i = (Indigency) request;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO indigencies (request_id, full_name, address, contact, gender, year_of_birth, date_requested, monthly_income, reason, supporting_document) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setString(1, i.getRequestId());
                    ps.setString(2, r.getFullName());
                    ps.setString(3, r.getAddress());
                    ps.setString(4, r.getContactNumber());
                    ps.setString(5, r.getGender());
                    ps.setInt(6, r.getYearOfBirth());
                    ps.setString(7, i.getDateRequested().toString());
                    ps.setDouble(8, i.getMonthlyIncome());
                    ps.setString(9, i.getReason());
                    ps.setString(10, i.getSupportingDocument());
                    ps.executeUpdate();
                }
            } else if (request instanceof Residency) {
                Residency res = (Residency) request;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO residencies (request_id, full_name, address, contact, gender, year_of_birth, date_requested, years_of_residency, months_of_residency) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setString(1, res.getRequestId());
                    ps.setString(2, r.getFullName());
                    ps.setString(3, r.getAddress());
                    ps.setString(4, r.getContactNumber());
                    ps.setString(5, r.getGender());
                    ps.setInt(6, r.getYearOfBirth());
                    ps.setString(7, res.getDateRequested().toString());
                    ps.setInt(8, res.getYearsOfResidency());
                    ps.setInt(9, res.getMonthsOfResidency());
                    ps.executeUpdate();
                }
            } else {
                throw new IllegalArgumentException("Unknown request type: " + request.getDocumentType());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error: " + e.getMessage(), e);
        }
    }

    public void insertRental(BrgyRental rental) {
        Resident r = rental.getResident();
        try (Connection conn = connect()) {
            if (rental instanceof CourtRental) {
                CourtRental c = (CourtRental) rental;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO court_rentals (rental_id, full_name, address, contact, gender, year_of_birth, rental_date, is_whole_day, start_hour, duration_hours) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setString(1, c.getRentalId());
                    ps.setString(2, r.getFullName());
                    ps.setString(3, r.getAddress());
                    ps.setString(4, r.getContactNumber());
                    ps.setString(5, r.getGender());
                    ps.setInt(6, r.getYearOfBirth());
                    ps.setString(7, c.getRentalDate().toString());
                    ps.setInt(8, c.isWholeDay() ? 1 : 0);   // SQLite has no boolean: 1 = yes, 0 = no
                    ps.setInt(9, c.getStartHour());
                    ps.setInt(10, c.getDurationHours());
                    ps.executeUpdate();
                }
            } else if (rental instanceof TentRental) {
                TentRental t = (TentRental) rental;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO tent_rentals (rental_id, full_name, address, contact, gender, year_of_birth, rental_date, quantity, number_of_days) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setString(1, t.getRentalId());
                    ps.setString(2, r.getFullName());
                    ps.setString(3, r.getAddress());
                    ps.setString(4, r.getContactNumber());
                    ps.setString(5, r.getGender());
                    ps.setInt(6, r.getYearOfBirth());
                    ps.setString(7, t.getRentalDate().toString());
                    ps.setInt(8, t.getQuantity());
                    ps.setInt(9, t.getNumberOfDays());
                    ps.executeUpdate();
                }
            } else {
                throw new IllegalArgumentException("Unknown rental type: " + rental.getRentalType());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error: " + e.getMessage(), e);
        }
    }

    //  UPDATE
    public void updateRequest(DocumentRequest request) {
        Resident r = request.getResident();
        int changed;
        try (Connection conn = connect()) {
            if (request instanceof Clearance) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE clearances SET full_name = ?, address = ?, contact = ?, gender = ?, year_of_birth = ?, purpose = ? "
                                + "WHERE request_id = ?")) {
                    ps.setString(1, r.getFullName());
                    ps.setString(2, r.getAddress());
                    ps.setString(3, r.getContactNumber());
                    ps.setString(4, r.getGender());
                    ps.setInt(5, r.getYearOfBirth());
                    ps.setString(6, ((Clearance) request).getPurpose());
                    ps.setString(7, request.getRequestId());
                    changed = ps.executeUpdate();
                }
            } else if (request instanceof Indigency) {
                Indigency i = (Indigency) request;
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE indigencies SET full_name = ?, address = ?, contact = ?, gender = ?, year_of_birth = ?, "
                                + "monthly_income = ?, reason = ?, supporting_document = ? WHERE request_id = ?")) {
                    ps.setString(1, r.getFullName());
                    ps.setString(2, r.getAddress());
                    ps.setString(3, r.getContactNumber());
                    ps.setString(4, r.getGender());
                    ps.setInt(5, r.getYearOfBirth());
                    ps.setDouble(6, i.getMonthlyIncome());
                    ps.setString(7, i.getReason());
                    ps.setString(8, i.getSupportingDocument());
                    ps.setString(9, request.getRequestId());
                    changed = ps.executeUpdate();
                }
            } else if (request instanceof Residency) {
                Residency res = (Residency) request;
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE residencies SET full_name = ?, address = ?, contact = ?, gender = ?, year_of_birth = ?, "
                                + "years_of_residency = ?, months_of_residency = ? WHERE request_id = ?")) {
                    ps.setString(1, r.getFullName());
                    ps.setString(2, r.getAddress());
                    ps.setString(3, r.getContactNumber());
                    ps.setString(4, r.getGender());
                    ps.setInt(5, r.getYearOfBirth());
                    ps.setInt(6, res.getYearsOfResidency());
                    ps.setInt(7, res.getMonthsOfResidency());
                    ps.setString(8, request.getRequestId());
                    changed = ps.executeUpdate();
                }
            } else {
                throw new IllegalArgumentException("Unknown request type: " + request.getDocumentType());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error: " + e.getMessage(), e);
        }
        if (changed == 0) {
            throw new IllegalStateException("Nothing was updated. ID not found in database: " + request.getRequestId());
        }
    }

    // Updates ONE saved rental
    public void updateRental(BrgyRental rental) {
        Resident r = rental.getResident();
        int changed;
        try (Connection conn = connect()) {
            if (rental instanceof CourtRental) {
                CourtRental c = (CourtRental) rental;
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE court_rentals SET full_name = ?, address = ?, contact = ?, gender = ?, year_of_birth = ?, "
                                + "rental_date = ?, is_whole_day = ?, start_hour = ?, duration_hours = ? WHERE rental_id = ?")) {
                    ps.setString(1, r.getFullName());
                    ps.setString(2, r.getAddress());
                    ps.setString(3, r.getContactNumber());
                    ps.setString(4, r.getGender());
                    ps.setInt(5, r.getYearOfBirth());
                    ps.setString(6, c.getRentalDate().toString());
                    ps.setInt(7, c.isWholeDay() ? 1 : 0);
                    ps.setInt(8, c.getStartHour());
                    ps.setInt(9, c.getDurationHours());
                    ps.setString(10, c.getRentalId());
                    changed = ps.executeUpdate();
                }
            } else if (rental instanceof TentRental) {
                TentRental t = (TentRental) rental;
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE tent_rentals SET full_name = ?, address = ?, contact = ?, gender = ?, year_of_birth = ?, "
                                + "rental_date = ?, quantity = ?, number_of_days = ? WHERE rental_id = ?")) {
                    ps.setString(1, r.getFullName());
                    ps.setString(2, r.getAddress());
                    ps.setString(3, r.getContactNumber());
                    ps.setString(4, r.getGender());
                    ps.setInt(5, r.getYearOfBirth());
                    ps.setString(6, t.getRentalDate().toString());
                    ps.setInt(7, t.getQuantity());
                    ps.setInt(8, t.getNumberOfDays());
                    ps.setString(9, t.getRentalId());
                    changed = ps.executeUpdate();
                }
            } else {
                throw new IllegalArgumentException("Unknown rental type: " + rental.getRentalType());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error: " + e.getMessage(), e);
        }
        if (changed == 0) {
            throw new IllegalStateException("Nothing was updated. ID not found in database: " + rental.getRentalId());
        }
    }

    //  DELETE=

    // Deletes ONE saved request by ID
    public void deleteRequest(String requestId) {
        try (Connection conn = connect()) {
            // Try each table; only the one that holds the ID removes a row
            for (String table : new String[]{"clearances", "indigencies", "residencies"}) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + " WHERE request_id = ?")) {
                    ps.setString(1, requestId);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error: " + e.getMessage(), e);
        }
    }

    // Deletes ONE saved rental by ID
    public void deleteRental(String rentalId) {
        try (Connection conn = connect()) {
            for (String table : new String[]{"court_rentals", "tent_rentals"}) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + " WHERE rental_id = ?")) {
                    ps.setString(1, rentalId);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error: " + e.getMessage(), e);
        }
    }

    // LOAD
    public List<DocumentRequest> loadRequests() {
        List<DocumentRequest> result = new ArrayList<>();
        try (Connection conn = connect(); Statement st = conn.createStatement()) {

            try (ResultSet rs = st.executeQuery("SELECT * FROM clearances")) {
                while (rs.next()) {
                    try {
                        result.add(new Clearance(readResident(rs), rs.getString("request_id"),
                                LocalDate.parse(rs.getString("date_requested")), rs.getString("purpose")));
                    } catch (RuntimeException ex) {
                        System.err.println("Skipped bad clearance row: " + ex.getMessage());
                    }
                }
            }

            try (ResultSet rs = st.executeQuery("SELECT * FROM indigencies")) {
                while (rs.next()) {
                    try {
                        result.add(new Indigency(readResident(rs), rs.getString("request_id"),
                                LocalDate.parse(rs.getString("date_requested")),
                                rs.getDouble("monthly_income"), rs.getString("reason"),
                                rs.getString("supporting_document")));
                    } catch (RuntimeException ex) {
                        System.err.println("Skipped bad indigency row: " + ex.getMessage());
                    }
                }
            }

            try (ResultSet rs = st.executeQuery("SELECT * FROM residencies")) {
                while (rs.next()) {
                    try {
                        result.add(new Residency(readResident(rs), rs.getString("request_id"),
                                LocalDate.parse(rs.getString("date_requested")),
                                rs.getInt("years_of_residency"), rs.getInt("months_of_residency")));
                    } catch (RuntimeException ex) {
                        System.err.println("Skipped bad residency row: " + ex.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error: " + e.getMessage(), e);
        }
        result.sort((a, b) -> Integer.compare(
                Integer.parseInt(a.getRequestId().substring(4)),
                Integer.parseInt(b.getRequestId().substring(4))));
        return result;
    }

    // Reads every saved rental from both rental tables (loading constructors)
    public List<BrgyRental> loadRentals() {
        List<BrgyRental> result = new ArrayList<>();
        try (Connection conn = connect(); Statement st = conn.createStatement()) {

            try (ResultSet rs = st.executeQuery("SELECT * FROM court_rentals")) {
                while (rs.next()) {
                    try {
                        result.add(new CourtRental(rs.getString("rental_id"), readResident(rs),
                                LocalDate.parse(rs.getString("rental_date")),
                                rs.getInt("is_whole_day") == 1,
                                rs.getInt("start_hour"), rs.getInt("duration_hours")));
                    } catch (RuntimeException ex) {
                        System.err.println("Skipped bad court rental row: " + ex.getMessage());
                    }
                }
            }

            try (ResultSet rs = st.executeQuery("SELECT * FROM tent_rentals")) {
                while (rs.next()) {
                    try {
                        result.add(new TentRental(rs.getString("rental_id"), readResident(rs),
                                LocalDate.parse(rs.getString("rental_date")),
                                rs.getInt("quantity"), rs.getInt("number_of_days")));
                    } catch (RuntimeException ex) {
                        System.err.println("Skipped bad tent rental row: " + ex.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error: " + e.getMessage(), e);
        }
        result.sort((x, y) -> Integer.compare(
                Integer.parseInt(x.getRentalId().substring(4)),
                Integer.parseInt(y.getRentalId().substring(4))));
        return result;
    }
}