package brgydocumentrequest.gui;

import brgydocumentrequest.model.*;
import brgydocumentrequest.persistence.RequestManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DashboardPanel extends JPanel {

    private JTabbedPane tabbedPane;
    private LogoutListener logoutListener;

    // keeps the records in memory AND saves them to the database
    private final RequestManager manager = new RequestManager();

    // Dashboard counter labels
    private JLabel residencyCountLabel = new JLabel("Records: 0");
    private JLabel indigencyCountLabel = new JLabel("Records: 0");
    private JLabel clearanceCountLabel = new JLabel("Records: 0");
    private JLabel courtRentalCountLabel = new JLabel("Records: 0");
    private JLabel tentRentalCountLabel = new JLabel("Records: 0");

    public interface LogoutListener {
        void onLogout();
    }

    public DashboardPanel(LogoutListener logoutListener) {
        // Read all saved records from the database BEFORE the tabs are built
        try {
            manager.loadAll();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        this.logoutListener = logoutListener;
        setLayout(new BorderLayout());

        //INITIALIZE TABBED PANE
        tabbedPane = new JTabbedPane();

        //PANELS
        tabbedPane.addTab("Home", createHomeContent());
        tabbedPane.addTab("Residency", createResidencyContent());
        tabbedPane.addTab("Indigency", createIndigencyContent());
        tabbedPane.addTab("Clearance", createClearanceContent());
        tabbedPane.addTab("Court Rental", createCourtRentalContent());
        tabbedPane.addTab("Tent Rental", createTentRentalContent());

        //SIDE BAR NAVIGATION
        JPanel sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBackground(new Color(41, 50, 65));
        sideBar.setPreferredSize(new Dimension(220, getHeight()));

        //SIDE BAR NAVIGATION BUTTONS
        sideBar.add(createNavButton("Home", 0));
        sideBar.add(Box.createRigidArea(new Dimension(0, 5)));
        sideBar.add(createNavButton("Residency Request", 1));
        sideBar.add(Box.createRigidArea(new Dimension(0, 5)));
        sideBar.add(createNavButton("Indigency Request", 2));
        sideBar.add(Box.createRigidArea(new Dimension(0, 5)));
        sideBar.add(createNavButton("Clearance Request", 3));
        sideBar.add(Box.createRigidArea(new Dimension(0, 5)));
        sideBar.add(createNavButton("Court Rental", 4));
        sideBar.add(Box.createRigidArea(new Dimension(0, 5)));
        sideBar.add(createNavButton("Tent Rental", 5));

        //PUSH LOGOUT BUTTON TO THE BOTTOM OF THE SIDEBAR
        sideBar.add(Box.createVerticalGlue());

        //LOGOUT BUTTON
        JButton logoutButton = new JButton("Logout");
        logoutButton.setMaximumSize(new Dimension(190, 40));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setBackground(new Color(244, 49, 49));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            if (logoutListener != null) {
                logoutListener.onLogout();
            }
        });

        sideBar.add(logoutButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 20)));

        //ADD COMPONENTS TO DASHBOARD
        add(sideBar, BorderLayout.WEST);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JButton createNavButton(String text, int tabIndex) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 35));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(new Color(41, 50, 65));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.addActionListener(e -> tabbedPane.setSelectedIndex(tabIndex));
        return button;
    }

    // Refreshes a dashboard counter using the number of rows in its table.
    private void updateDashboardCount(JLabel countLabel, DefaultTableModel model) {
        countLabel.setText("Records: " + model.getRowCount());
    }

    private void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showDbError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    // FOR UPDATE
    private void replaceRow(DefaultTableModel model, int row, Object[] values) {
        for (int col = 0; col < values.length; col++) {
            model.setValueAt(values[col], row, col);
        }
    }

    //  TABLE row for add and update

    private Object[] residencyRow(Residency x) {
        return new Object[]{
                x.getRequestId(),
                x.getResidentName(),
                x.getResident().getAddress(),
                x.getResident().getContactNumber(),
                x.getResident().getGender(),
                x.getResident().getYearOfBirth(),
                x.getDateRequested().toString(),
                x.getYearsOfResidency(),
                x.getMonthsOfResidency(),
                x.getPaymentDisplay()
        };
    }

    private Object[] indigencyRow(Indigency x) {
        return new Object[]{
                x.getRequestId(),
                x.getResidentName(),
                x.getResident().getAddress(),
                x.getResident().getContactNumber(),
                x.getResident().getGender(),
                x.getResident().getYearOfBirth(),
                x.getDateRequested().toString(),
                x.getMonthlyIncome(),
                x.getReason(),
                x.getSupportingDocument(),
                "FREE"
        };
    }

    private Object[] clearanceRow(Clearance x) {
        return new Object[]{
                x.getRequestId(),
                x.getResidentName(),
                x.getResident().getAddress(),
                x.getResident().getContactNumber(),
                x.getResident().getGender(),
                x.getResident().getYearOfBirth(),
                x.getDateRequested().toString(),
                x.getPurpose(),
                x.getPaymentDisplay()
        };
    }

    private Object[] courtRow(CourtRental x) {
        return new Object[]{
                x.getRentalId(),
                x.getResident().getFullName(),
                x.getResident().getAddress(),
                x.getResident().getContactNumber(),
                x.getResident().getGender(),
                x.getResident().getYearOfBirth(),
                x.getRentalType(),
                x.getRentalDate().toString(),
                x.isWholeDay() ? "Whole Day" : x.getStartHour() + ":00 for " + x.getDurationHours() + " hrs",
                x.getPaymentDisplay()
        };
    }

    private Object[] tentRow(TentRental x) {
        return new Object[]{
                x.getRentalId(),
                x.getResident().getFullName(),
                x.getResident().getAddress(),
                x.getResident().getContactNumber(),
                x.getResident().getGender(),
                x.getResident().getYearOfBirth(),
                x.getQuantity(),
                x.getNumberOfDays(),
                x.getRentalDate().toString(),
                x.getPaymentDisplay()
        };
    }

    // HOME TAB

    private JPanel createHomeContent() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        panel.add(createRequestCard("Residency Requests", residencyCountLabel));
        panel.add(createRequestCard("Indigency Requests", indigencyCountLabel));
        panel.add(createRequestCard("Clearance Requests", clearanceCountLabel));
        panel.add(createRequestCard("Court Rentals", courtRentalCountLabel));
        panel.add(createRequestCard("Tent Rentals", tentRentalCountLabel));

        return panel;
    }

    private JPanel createRequestCard(String title, JLabel countLabel) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 223), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(200, 120));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        countLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);
        return card;
    }

    //RESIDENCY REQUEST TAB
    private JPanel createResidencyContent() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 247, 250));

        String[] columnNames = {
                "Request ID", "Full Name", "Address", "Contact Number",
                "Gender", "Year of Birth", "Date Requested",
                "Years", "Months", "Purpose", "Fee"
        };

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Residency> requestList = new ArrayList<>();

        // LOAD SAVED REQUESTS
        for (DocumentRequest r : manager.getAll()) {
            if (r instanceof Residency) {
                Residency x = (Residency) r;
                requestList.add(x);
                tableModel.addRow(residencyRow(x));
            }
        }

        updateDashboardCount(residencyCountLabel, tableModel);

        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // INPUT FORM PANEL
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Residency Certificate Request"));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtFullName = new JTextField(12);
        JTextField txtAddress = new JTextField(12);
        JTextField txtContactNumber = new JTextField(12);

        JComboBox<String> cmbGender = new JComboBox<>(
                new String[]{"Male", "Female", "Other"}
        );

        JTextField txtYearOfBirth = new JTextField(12);

        JSpinner spnYears = new JSpinner(
                new SpinnerNumberModel(1, 0, 50, 1)
        );

        JSpinner spnMonths = new JSpinner(
                new SpinnerNumberModel(0, 0, 11, 1)
        );

        // PURPOSE COMBOBOX
        String[] purposes = {
                "Employment",
                "School Requirement",
                "Business Requirement",
                "Government Transaction",
                "Personal Use",
                "Others"
        };

        JComboBox<String> cmbPurpose = new JComboBox<>(purposes);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        formPanel.add(txtFullName, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 3;
        formPanel.add(txtAddress, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Contact Number:"), gbc);

        gbc.gridx = 1;
        formPanel.add(txtContactNumber, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Gender:"), gbc);

        gbc.gridx = 3;
        formPanel.add(cmbGender, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Year of Birth:"), gbc);

        gbc.gridx = 1;
        formPanel.add(txtYearOfBirth, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Purpose:"), gbc);

        gbc.gridx = 3;
        formPanel.add(cmbPurpose, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Years of Residency:"), gbc);

        gbc.gridx = 1;
        formPanel.add(spnYears, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Months of Residency:"), gbc);

        gbc.gridx = 3;
        formPanel.add(spnMonths, gbc);

        // ACTION BUTTONS
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = new JButton("Submit");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear Form");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;

        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.SOUTH);

        // TABLE SELECTION
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();

            if (!e.getValueIsAdjusting() && row != -1) {
                txtFullName.setText(tableModel.getValueAt(row, 1).toString());
                txtAddress.setText(tableModel.getValueAt(row, 2).toString());
                txtContactNumber.setText(tableModel.getValueAt(row, 3).toString());
                cmbGender.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                txtYearOfBirth.setText(tableModel.getValueAt(row, 5).toString());

                spnYears.setValue(Integer.parseInt(tableModel.getValueAt(row, 7).toString()));
                spnMonths.setValue(Integer.parseInt(tableModel.getValueAt(row, 8).toString()));
                cmbPurpose.setSelectedItem(tableModel.getValueAt(row, 9).toString());
            }
        });

        // CREATE ACTION
        btnAdd.addActionListener(e -> {
            try {
                String name = txtFullName.getText().trim();
                String address = txtAddress.getText().trim();
                String contactNumber = txtContactNumber.getText().trim();
                String gender = (String) cmbGender.getSelectedItem();

                int yearOfBirth = Integer.parseInt(txtYearOfBirth.getText().trim());

                int years = (Integer) spnYears.getValue();
                int months = (Integer) spnMonths.getValue();

                String purpose = (String) cmbPurpose.getSelectedItem();

                Resident resident = new Resident(
                        name, address, contactNumber, gender, yearOfBirth
                );

                Residency residency = new Residency(resident, years, months);

                manager.addRequest(residency);

                requestList.add(residency);
                tableModel.addRow(residencyRow(residency));

                updateDashboardCount(residencyCountLabel, tableModel);

                JOptionPane.showMessageDialog(
                        panel,
                        "Residency Request Created Successfully!"
                );

            } catch (NumberFormatException ex) {
                showError(panel, "Year of birth must be a valid whole number.");
            } catch (IllegalArgumentException ex) {
                showError(panel, ex.getMessage());
            } catch (IllegalStateException ex) {
                showDbError(panel, ex.getMessage());
            }
        });

        // UPDATE ACTION
        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(
                        panel,
                        "Please select a request to update.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                Residency old = requestList.get(row);

                String name = txtFullName.getText().trim();
                String address = txtAddress.getText().trim();
                String contactNumber = txtContactNumber.getText().trim();
                String gender = (String) cmbGender.getSelectedItem();

                int yearOfBirth = Integer.parseInt(txtYearOfBirth.getText().trim());

                int years = (Integer) spnYears.getValue();
                int months = (Integer) spnMonths.getValue();

                String purpose = (String) cmbPurpose.getSelectedItem();

                Resident resident = new Resident(
                        name, address, contactNumber, gender, yearOfBirth
                );

                Residency updated = new Residency(
                        resident,
                        old.getRequestId(),
                        old.getDateRequested(),
                        years,
                        months
                );

                manager.updateRequest(updated);

                requestList.set(row, updated);
                replaceRow(tableModel, row, residencyRow(updated));

                JOptionPane.showMessageDialog(
                        panel,
                        "Residency Request Updated Successfully!"
                );

            } catch (NumberFormatException ex) {
                showError(panel, "Year of birth must be a valid whole number.");
            } catch (IllegalArgumentException ex) {
                showError(panel, ex.getMessage());
            } catch (IllegalStateException ex) {
                showDbError(panel, ex.getMessage());
            }
        });

        // DELETE ACTION
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(
                        panel,
                        "Please select a request to delete.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    panel,
                    "Are you sure you want to delete this record?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    manager.deleteRequest(
                            tableModel.getValueAt(row, 0).toString()
                    );

                    requestList.remove(row);
                    tableModel.removeRow(row);

                    updateDashboardCount(residencyCountLabel, tableModel);

                    JOptionPane.showMessageDialog(panel, "Request Deleted.");

                } catch (IllegalArgumentException ex) {
                    showError(panel, ex.getMessage());
                } catch (IllegalStateException ex) {
                    showDbError(panel, ex.getMessage());
                }
            }
        });

        // CLEAR FORM ACTION
        btnClear.addActionListener(e -> {
            txtFullName.setText("");
            txtAddress.setText("");
            txtContactNumber.setText("");
            cmbGender.setSelectedIndex(0);
            txtYearOfBirth.setText("");

            spnYears.setValue(1);
            spnMonths.setValue(0);

            cmbPurpose.setSelectedIndex(0);

            table.clearSelection();
        });

        return panel;
    }

    // INDIGENCY REQUEST TAB
    private JPanel createIndigencyContent() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 247, 250));

        String[] columnNames = {"Request ID", "Full Name", "Address", "Contact Number", "Gender", "Year of Birth", "Date Requested", "Monthly Income", "Reason", "Supporting Doc", "Fee"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Requests, same order as the table rows (keeps the original ID and date when updating)
        List<Indigency> requestList = new ArrayList<>();

        // LOAD the saved brgydocumentrequest.model.Indigency records from the database
        for (DocumentRequest r : manager.getAll()) {
            if (r instanceof Indigency) {
                Indigency x = (Indigency) r;
                requestList.add(x);
                tableModel.addRow(indigencyRow(x));
            }
        }
        updateDashboardCount(indigencyCountLabel, tableModel);

        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // INPUT FORM PANEL
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Indigency Request"));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // NOTE: the request date is set automatically (today) by brgydocumentrequest.model.DocumentRequest, so there is no date field here
        JTextField txtFullName = new JTextField(12);
        JTextField txtAddress = new JTextField(12);
        JTextField txtContactNumber = new JTextField("", 12);
        JComboBox<String> cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField txtYearOfBirth = new JTextField("", 12);
        JTextField txtIncome = new JTextField("", 12);
        JTextField txtReason = new JTextField(12);
        JComboBox<String> cmbSupportingDoc = new JComboBox<>(new String[]{
                "--Select a Supporting Document--", "Certification", "DSWD Record", "Affidavit of No Income", "Barangay Health Worker Note"
        });

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Full Name: "), gbc);
        gbc.gridx = 1; formPanel.add(txtFullName, gbc);
        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(new JLabel("Address: "), gbc);
        gbc.gridx = 3; formPanel.add(txtAddress, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Contact Number: "), gbc);
        gbc.gridx = 1; formPanel.add(txtContactNumber, gbc);
        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Gender: "), gbc);
        gbc.gridx = 3; formPanel.add(cmbGender, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Year of Birth: "), gbc);
        gbc.gridx = 1; formPanel.add(txtYearOfBirth, gbc);

        gbc.gridx = 4; gbc.gridy = 0; formPanel.add(new JLabel("Monthly Income (<=10k): "), gbc);
        gbc.gridx = 5; formPanel.add(txtIncome, gbc);
        gbc.gridx = 4; gbc.gridy = 1; formPanel.add(new JLabel("Reason: "), gbc);
        gbc.gridx = 5; formPanel.add(txtReason, gbc);
        gbc.gridx = 4; gbc.gridy = 2; formPanel.add(new JLabel("Supporting Doc: "), gbc);
        gbc.gridx = 5; formPanel.add(cmbSupportingDoc, gbc);

        // ACTION BUTTONS
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnAdd = new JButton("Submit");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear Form");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 6;
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.SOUTH);

        // LISTENERS
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row != -1) {
                txtFullName.setText(tableModel.getValueAt(row, 1).toString());
                txtAddress.setText(tableModel.getValueAt(row, 2).toString());
                txtContactNumber.setText(tableModel.getValueAt(row, 3).toString());
                cmbGender.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                txtYearOfBirth.setText(tableModel.getValueAt(row, 5).toString());
                txtIncome.setText(tableModel.getValueAt(row, 7).toString());
                txtReason.setText(tableModel.getValueAt(row, 8).toString());
                cmbSupportingDoc.setSelectedItem(tableModel.getValueAt(row, 9).toString());
            }
        });

        //CREATE ACTION
        btnAdd.addActionListener(e -> {
            try {
                String name = txtFullName.getText().trim();
                String address = txtAddress.getText().trim();
                String contactNumber = txtContactNumber.getText().trim();
                String gender = (String) cmbGender.getSelectedItem();
                int yearOfBirth = Integer.parseInt(txtYearOfBirth.getText().trim());
                double income = Double.parseDouble(txtIncome.getText().trim());
                String reason = txtReason.getText().trim();
                String supportingDoc = (String) cmbSupportingDoc.getSelectedItem();

                Resident resident = new Resident(name, address, contactNumber, gender, yearOfBirth);
                Indigency indigency = new Indigency(resident, income, reason, supportingDoc);

                // Validate + SAVE TO DATABASE
                manager.addRequest(indigency);

                requestList.add(indigency);
                tableModel.addRow(indigencyRow(indigency));
                updateDashboardCount(indigencyCountLabel, tableModel);
                JOptionPane.showMessageDialog(panel, "Indigency Request Created Successfully!");
            } catch (NumberFormatException ex) {
                showError(panel, "Year of birth and monthly income must be valid numbers");
            } catch (IllegalArgumentException ex) {
                showError(panel, ex.getMessage());
            } catch (IllegalStateException ex) {
                showDbError(panel, ex.getMessage());
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a request to update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Indigency old = requestList.get(row);
                String name = txtFullName.getText().trim();
                String address = txtAddress.getText().trim();
                String contactNumber = txtContactNumber.getText().trim();
                String gender = (String) cmbGender.getSelectedItem();
                int yearOfBirth = Integer.parseInt(txtYearOfBirth.getText().trim());
                double income = Double.parseDouble(txtIncome.getText().trim());
                String reason = txtReason.getText().trim();
                String supportingDoc = (String) cmbSupportingDoc.getSelectedItem();

                Resident resident = new Resident(name, address, contactNumber, gender, yearOfBirth);

                // Database constructor: keeps the original ID and date requested
                Indigency updated = new Indigency(resident, old.getRequestId(), old.getDateRequested(), income, reason, supportingDoc);

                // UPDATE IN DATABASE
                manager.updateRequest(updated);

                requestList.set(row, updated);
                replaceRow(tableModel, row, indigencyRow(updated));
                JOptionPane.showMessageDialog(panel, "Indigency Request Updated Successfully!");
            } catch (NumberFormatException ex) {
                showError(panel, "Year of birth and monthly income must be valid numbers");
            } catch (IllegalArgumentException ex) {
                showError(panel, ex.getMessage());
            } catch (IllegalStateException ex) {
                showDbError(panel, ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a request to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete this record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // DELETE FROM DATABASE
                    manager.deleteRequest(tableModel.getValueAt(row, 0).toString());

                    requestList.remove(row);
                    tableModel.removeRow(row);
                    updateDashboardCount(indigencyCountLabel, tableModel);
                    JOptionPane.showMessageDialog(panel, "Request Deleted.");
                } catch (IllegalArgumentException ex) {
                    showError(panel, ex.getMessage());
                } catch (IllegalStateException ex) {
                    showDbError(panel, ex.getMessage());
                }
            }
        });

        // CLEAR FORM ACTION
        btnClear.addActionListener(e -> {
            txtFullName.setText("");
            txtAddress.setText("");
            txtContactNumber.setText("");
            cmbGender.setSelectedIndex(0);
            txtYearOfBirth.setText("");
            txtIncome.setText("");
            txtReason.setText("");
            cmbSupportingDoc.setSelectedIndex(0);
            table.clearSelection();
        });

        return panel;
    }

    // CLEARANCE REQUEST TAB
    private JPanel createClearanceContent() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 247, 250));

        String[] columnNames = {"Request ID", "Full Name", "Address", "Contact Number", "Gender", "Year of Birth", "Date Requested", "Purpose", "Fee"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Requests, same order as the table rows (keeps the original ID and date when updating)
        List<Clearance> requestList = new ArrayList<>();

        // LOAD the saved brgydocumentrequest.model.Clearance records from the database
        for (DocumentRequest r : manager.getAll()) {
            if (r instanceof Clearance) {
                Clearance x = (Clearance) r;
                requestList.add(x);
                tableModel.addRow(clearanceRow(x));
            }
        }
        updateDashboardCount(clearanceCountLabel, tableModel);

        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // INPUT FORM PANEL
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Barangay Clearance Request"));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // NOTE: the request date is set automatically (today) by brgydocumentrequest.model.DocumentRequest, so there is no date field here
        JTextField txtFullName = new JTextField(12);
        JTextField txtAddress = new JTextField(12);
        JTextField txtContactNumber = new JTextField("", 12);
        JComboBox<String> cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField txtYearOfBirth = new JTextField("", 12);
        JTextField txtPurpose = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Full Name: "), gbc);
        gbc.gridx = 1; formPanel.add(txtFullName, gbc);
        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(new JLabel("Address: "), gbc);
        gbc.gridx = 3; formPanel.add(txtAddress, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Contact Number: "), gbc);
        gbc.gridx = 1; formPanel.add(txtContactNumber, gbc);
        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Gender: "), gbc);
        gbc.gridx = 3; formPanel.add(cmbGender, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Year of Birth: "), gbc);
        gbc.gridx = 1; formPanel.add(txtYearOfBirth, gbc);
        gbc.gridx = 2; gbc.gridy = 2; formPanel.add(new JLabel("Purpose: "), gbc);
        gbc.gridx = 3; formPanel.add(txtPurpose, gbc);

        // ACTION BUTTONS
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnAdd = new JButton("Submit");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear Form");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.SOUTH);

        // LISTENERS
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row != -1) {
                txtFullName.setText(tableModel.getValueAt(row, 1).toString());
                txtAddress.setText(tableModel.getValueAt(row, 2).toString());
                txtContactNumber.setText(tableModel.getValueAt(row, 3).toString());
                cmbGender.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                txtYearOfBirth.setText(tableModel.getValueAt(row, 5).toString());
                txtPurpose.setText(tableModel.getValueAt(row, 7).toString());
            }
        });

        //CREATE ACTION
        btnAdd.addActionListener(e -> {
            try {
                String name = txtFullName.getText().trim();
                String address = txtAddress.getText().trim();
                String contactNumber = txtContactNumber.getText().trim();
                String gender = (String) cmbGender.getSelectedItem();
                int yearOfBirth = Integer.parseInt(txtYearOfBirth.getText().trim());
                String purpose = txtPurpose.getText().trim();

                Resident resident = new Resident(name, address, contactNumber, gender, yearOfBirth);
                Clearance clearance = new Clearance(resident, purpose);

                // Validate + SAVE TO DATABASE
                manager.addRequest(clearance);

                requestList.add(clearance);
                tableModel.addRow(clearanceRow(clearance));
                updateDashboardCount(clearanceCountLabel, tableModel);
                JOptionPane.showMessageDialog(panel, "Clearance Request Created Successfully!");
            } catch (NumberFormatException ex) {
                showError(panel, "Year of birth must be a valid whole number");
            } catch (IllegalArgumentException ex) {
                showError(panel, ex.getMessage());
            } catch (IllegalStateException ex) {
                showDbError(panel, ex.getMessage());
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a request to update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Clearance old = requestList.get(row);
                String name = txtFullName.getText().trim();
                String address = txtAddress.getText().trim();
                String contactNumber = txtContactNumber.getText().trim();
                String gender = (String) cmbGender.getSelectedItem();
                int yearOfBirth = Integer.parseInt(txtYearOfBirth.getText().trim());
                String purpose = txtPurpose.getText().trim();

                Resident resident = new Resident(name, address, contactNumber, gender, yearOfBirth);

                // Database constructor: keeps the original ID and date requested
                Clearance updated = new Clearance(resident, old.getRequestId(), old.getDateRequested(), purpose);

                // UPDATE IN DATABASE
                manager.updateRequest(updated);

                requestList.set(row, updated);
                replaceRow(tableModel, row, clearanceRow(updated));
                JOptionPane.showMessageDialog(panel, "Clearance Request Updated Successfully!");
            } catch (NumberFormatException ex) {
                showError(panel, "Year of birth must be a valid whole number");
            } catch (IllegalArgumentException ex) {
                showError(panel, ex.getMessage());
            } catch (IllegalStateException ex) {
                showDbError(panel, ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a request to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete this record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // DELETE FROM DATABASE
                    manager.deleteRequest(tableModel.getValueAt(row, 0).toString());

                    requestList.remove(row);
                    tableModel.removeRow(row);
                    updateDashboardCount(clearanceCountLabel, tableModel);
                    JOptionPane.showMessageDialog(panel, "Request Deleted.");
                } catch (IllegalArgumentException ex) {
                    showError(panel, ex.getMessage());
                } catch (IllegalStateException ex) {
                    showDbError(panel, ex.getMessage());
                }
            }
        });

        // CLEAR FORM ACTION
        btnClear.addActionListener(e -> {
            txtFullName.setText("");
            txtAddress.setText("");
            txtContactNumber.setText("");
            cmbGender.setSelectedIndex(0);
            txtYearOfBirth.setText("");
            txtPurpose.setText("");
            table.clearSelection();
        });

        return panel;
    }

    // COURT RENTAL TAB
    private JPanel createCourtRentalContent() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 247, 250));

        String[] columnNames = {"ID", "Renter Name", "Address", "Contact Number", "Gender", "Year of Birth", "Type", "Date", "Time Details", "Fee"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Court rentals, same order as the table rows
        List<CourtRental> courtList = new ArrayList<>();

        // LOAD the saved Court rentals from the database
        for (BrgyRental r : manager.getAllRentals()) {
            if (r instanceof CourtRental) {
                CourtRental x = (CourtRental) r;
                courtList.add(x);
                tableModel.addRow(courtRow(x));
            }
        }
        updateDashboardCount(courtRentalCountLabel, tableModel);

        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // INPUT FORM PANEL
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Court Rental Management (Hourly ₱500 / Whole Day ₱10,000)"));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtFullName = new JTextField(12);
        JTextField txtAddress = new JTextField(12);
        JTextField txtContactNumber = new JTextField("", 12);
        JComboBox<String> cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField txtYearOfBirth = new JTextField("", 12);
        JTextField txtDate = new JTextField(LocalDate.now().plusDays(1).toString(), 12);
        JCheckBox chkWholeDay = new JCheckBox("Whole Day Rental (₱10,000)");
        chkWholeDay.setBackground(Color.WHITE);
        JTextField txtStartHour = new JTextField("", 6);
        JTextField txtDuration = new JTextField("", 6);

        // Whole day rentals do not need a start hour / duration
        chkWholeDay.addItemListener(e -> {
            boolean hourly = !chkWholeDay.isSelected();
            txtStartHour.setEnabled(hourly);
            txtDuration.setEnabled(hourly);
        });

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Renter Name: "), gbc);
        gbc.gridx = 1; formPanel.add(txtFullName, gbc);
        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(new JLabel("Address: "), gbc);
        gbc.gridx = 3; formPanel.add(txtAddress, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Contact Number: "), gbc);
        gbc.gridx = 1; formPanel.add(txtContactNumber, gbc);
        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Gender: "), gbc);
        gbc.gridx = 3; formPanel.add(cmbGender, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Year of Birth: "), gbc);
        gbc.gridx = 1; formPanel.add(txtYearOfBirth, gbc);
        gbc.gridx = 2; gbc.gridy = 2; formPanel.add(new JLabel("Date (YYYY-MM-DD): "), gbc);
        gbc.gridx = 3; formPanel.add(txtDate, gbc);

        gbc.gridx = 4; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(chkWholeDay, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 4; gbc.gridy = 1; formPanel.add(new JLabel("Start Hour (6-21): "), gbc);
        gbc.gridx = 5; formPanel.add(txtStartHour, gbc);
        gbc.gridx = 4; gbc.gridy = 2; formPanel.add(new JLabel("Duration (Hours): "), gbc);
        gbc.gridx = 5; formPanel.add(txtDuration, gbc);

        // ACTION BUTTONS
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear Form");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 6;
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.SOUTH);

        // LISTENERS
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row != -1) {
                txtFullName.setText(tableModel.getValueAt(row, 1).toString());
                txtAddress.setText(tableModel.getValueAt(row, 2).toString());
                txtContactNumber.setText(tableModel.getValueAt(row, 3).toString());
                cmbGender.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                txtYearOfBirth.setText(tableModel.getValueAt(row, 5).toString());

                boolean wholeDay = tableModel.getValueAt(row, 6).toString().contains("Whole Day");
                chkWholeDay.setSelected(wholeDay);
                txtDate.setText(tableModel.getValueAt(row, 7).toString());

                // Time details look like "8:00 for 2 hrs" for hourly rentals
                if (!wholeDay) {
                    String[] parts = tableModel.getValueAt(row, 8).toString().split(":00 for | hrs");
                    txtStartHour.setText(parts[0]);
                    txtDuration.setText(parts[1].trim());
                }
            }
        });

        //CREATE ACTION
        btnAdd.addActionListener(e -> {
            try {
                String name = txtFullName.getText().trim();
                String address = txtAddress.getText().trim();
                String contactNumber = txtContactNumber.getText().trim();
                String gender = (String) cmbGender.getSelectedItem();
                int yearOfBirth = Integer.parseInt(txtYearOfBirth.getText().trim());
                LocalDate date = LocalDate.parse(txtDate.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                boolean isWholeDay = chkWholeDay.isSelected();
                int startHour = isWholeDay ? 0 : Integer.parseInt(txtStartHour.getText().trim());
                int duration = isWholeDay ? 0 : Integer.parseInt(txtDuration.getText().trim());

                Resident resident = new Resident(name, address, contactNumber, gender, yearOfBirth);
                CourtRental rental = new CourtRental(resident, date, isWholeDay, startHour, duration);

                // Checks the schedule clash (throws if the court is taken) + SAVES TO DATABASE
                manager.addRental(rental);

                courtList.add(rental);
                tableModel.addRow(courtRow(rental));
                updateDashboardCount(courtRentalCountLabel, tableModel);
                JOptionPane.showMessageDialog(panel, "Court Rental Added Successfully!");
            } catch (NumberFormatException ex) {
                showError(panel, "Year of birth, start hour and duration must be valid whole numbers");
            } catch (DateTimeParseException ex) {
                showError(panel, "Date must be in YYYY-MM-DD format");
            } catch (IllegalArgumentException ex) {
                showError(panel, ex.getMessage());
            } catch (IllegalStateException ex) {
                showDbError(panel, ex.getMessage());
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a rental to update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String id = tableModel.getValueAt(row, 0).toString();
                String name = txtFullName.getText().trim();
                String address = txtAddress.getText().trim();
                String contactNumber = txtContactNumber.getText().trim();
                String gender = (String) cmbGender.getSelectedItem();
                int yearOfBirth = Integer.parseInt(txtYearOfBirth.getText().trim());
                LocalDate date = LocalDate.parse(txtDate.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                boolean isWholeDay = chkWholeDay.isSelected();
                int startHour = isWholeDay ? 0 : Integer.parseInt(txtStartHour.getText().trim());
                int duration = isWholeDay ? 0 : Integer.parseInt(txtDuration.getText().trim());

                Resident resident = new Resident(name, address, contactNumber, gender, yearOfBirth);

                // Loaded-record constructor: keeps the original rental ID
                CourtRental updated = new CourtRental(id, resident, date, isWholeDay, startHour, duration);

                // The manager checks against all the OTHER bookings + UPDATES THE DATABASE
                manager.updateRental(updated);

                courtList.set(row, updated);
                replaceRow(tableModel, row, courtRow(updated));
                JOptionPane.showMessageDialog(panel, "Court Rental Updated Successfully!");
            } catch (NumberFormatException ex) {
                showError(panel, "Year of birth, start hour and duration must be valid whole numbers");
            } catch (DateTimeParseException ex) {
                showError(panel, "Date must be in YYYY-MM-DD format");
            } catch (IllegalArgumentException ex) {
                showError(panel, ex.getMessage());
            } catch (IllegalStateException ex) {
                showDbError(panel, ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a rental to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete this record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // DELETE FROM DATABASE
                    manager.deleteRental(tableModel.getValueAt(row, 0).toString());

                    courtList.remove(row);
                    tableModel.removeRow(row);
                    updateDashboardCount(courtRentalCountLabel, tableModel);
                    JOptionPane.showMessageDialog(panel, "Rental Deleted.");
                } catch (IllegalArgumentException ex) {
                    showError(panel, ex.getMessage());
                } catch (IllegalStateException ex) {
                    showDbError(panel, ex.getMessage());
                }
            }
        });

        // CLEAR FORM ACTION
        btnClear.addActionListener(e -> {
            txtFullName.setText("");
            txtAddress.setText("");
            txtContactNumber.setText("");
            cmbGender.setSelectedIndex(0);
            txtYearOfBirth.setText("");
            txtDate.setText(LocalDate.now().plusDays(1).toString());
            chkWholeDay.setSelected(false);
            txtStartHour.setText("8");
            txtDuration.setText("2");
            txtStartHour.setEnabled(true);
            txtDuration.setEnabled(true);
            table.clearSelection();
        });

        return panel;
    }

    // TENT RENTAL TAB
    private JPanel createTentRentalContent() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(245, 247, 250));

        String[] columnNames = {"ID", "Renter Name", "Address", "Contact Number", "Gender", "Year of Birth", "Quantity", "Days", "Date", "Fee"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Tent rentals, same order as the table rows
        List<TentRental> tentList = new ArrayList<>();

        // LOAD the saved Tent rentals from the database
        for (BrgyRental r : manager.getAllRentals()) {
            if (r instanceof TentRental) {
                TentRental x = (TentRental) r;
                tentList.add(x);
                tableModel.addRow(tentRow(x));
            }
        }
        updateDashboardCount(tentRentalCountLabel, tableModel);

        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // INPUT FORM PANEL
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Tent Rental Management (Max 30 Stock / Days Tracking)"));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtFullName = new JTextField(12);
        JTextField txtAddress = new JTextField(12);
        JTextField txtContactNumber = new JTextField("", 12);
        JComboBox<String> cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField txtYearOfBirth = new JTextField("", 12);
        JTextField txtDate = new JTextField(LocalDate.now().plusDays(1).toString(), 12);
        JTextField txtQuantity = new JTextField("", 6);
        JTextField txtDays = new JTextField("", 6);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Renter Name: "), gbc);
        gbc.gridx = 1; formPanel.add(txtFullName, gbc);
        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(new JLabel("Address: "), gbc);
        gbc.gridx = 3; formPanel.add(txtAddress, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Contact Number: "), gbc);
        gbc.gridx = 1; formPanel.add(txtContactNumber, gbc);
        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Gender: "), gbc);
        gbc.gridx = 3; formPanel.add(cmbGender, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Year of Birth: "), gbc);
        gbc.gridx = 1; formPanel.add(txtYearOfBirth, gbc);
        gbc.gridx = 2; gbc.gridy = 2; formPanel.add(new JLabel("Date (YYYY-MM-DD): "), gbc);
        gbc.gridx = 3; formPanel.add(txtDate, gbc);

        gbc.gridx = 4; gbc.gridy = 0; formPanel.add(new JLabel("Quantity (Max 30): "), gbc);
        gbc.gridx = 5; formPanel.add(txtQuantity, gbc);
        gbc.gridx = 4; gbc.gridy = 1; formPanel.add(new JLabel("Number of Days: "), gbc);
        gbc.gridx = 5; formPanel.add(txtDays, gbc);

        // ACTION BUTTONS
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear Form");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 6;
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.SOUTH);

        // LISTENERS
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row != -1) {
                txtFullName.setText(tableModel.getValueAt(row, 1).toString());
                txtAddress.setText(tableModel.getValueAt(row, 2).toString());
                txtContactNumber.setText(tableModel.getValueAt(row, 3).toString());
                cmbGender.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                txtYearOfBirth.setText(tableModel.getValueAt(row, 5).toString());
                txtQuantity.setText(tableModel.getValueAt(row, 6).toString());
                txtDays.setText(tableModel.getValueAt(row, 7).toString());
                txtDate.setText(tableModel.getValueAt(row, 8).toString());
            }
        });

        //CREATE ACTION
        btnAdd.addActionListener(e -> {
            try {
                String name = txtFullName.getText().trim();
                String address = txtAddress.getText().trim();
                String contactNumber = txtContactNumber.getText().trim();
                String gender = (String) cmbGender.getSelectedItem();
                int yearOfBirth = Integer.parseInt(txtYearOfBirth.getText().trim());
                int qty = Integer.parseInt(txtQuantity.getText().trim());
                int days = Integer.parseInt(txtDays.getText().trim());
                LocalDate date = LocalDate.parse(txtDate.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);

                Resident resident = new Resident(name, address, contactNumber, gender, yearOfBirth);
                TentRental rental = new TentRental(resident, date, qty, days);

                // Checks the 30-tent stock (throws if not enough) + SAVES TO DATABASE
                manager.addRental(rental);

                tentList.add(rental);
                tableModel.addRow(tentRow(rental));
                updateDashboardCount(tentRentalCountLabel, tableModel);
                JOptionPane.showMessageDialog(panel, "Tent Rental Added Successfully!");
            } catch (NumberFormatException ex) {
                showError(panel, "Year of birth, quantity and number of days must be valid whole numbers");
            } catch (DateTimeParseException ex) {
                showError(panel, "Date must be in YYYY-MM-DD format");
            } catch (IllegalArgumentException ex) {
                showError(panel, ex.getMessage());
            } catch (IllegalStateException ex) {
                showDbError(panel, ex.getMessage());
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a rental to update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String id = tableModel.getValueAt(row, 0).toString();
                String name = txtFullName.getText().trim();
                String address = txtAddress.getText().trim();
                String contactNumber = txtContactNumber.getText().trim();
                String gender = (String) cmbGender.getSelectedItem();
                int yearOfBirth = Integer.parseInt(txtYearOfBirth.getText().trim());
                int qty = Integer.parseInt(txtQuantity.getText().trim());
                int days = Integer.parseInt(txtDays.getText().trim());
                LocalDate date = LocalDate.parse(txtDate.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);

                Resident resident = new Resident(name, address, contactNumber, gender, yearOfBirth);

                // Loaded-record constructor: keeps the original rental ID
                TentRental updated = new TentRental(id, resident, date, qty, days);

                // The manager checks the stock against all the OTHER bookings + UPDATES THE DATABASE
                manager.updateRental(updated);

                tentList.set(row, updated);
                replaceRow(tableModel, row, tentRow(updated));
                JOptionPane.showMessageDialog(panel, "Tent Rental Updated Successfully!");
            } catch (NumberFormatException ex) {
                showError(panel, "Year of birth, quantity and number of days must be valid whole numbers");
            } catch (DateTimeParseException ex) {
                showError(panel, "Date must be in YYYY-MM-DD format");
            } catch (IllegalArgumentException ex) {
                showError(panel, ex.getMessage());
            } catch (IllegalStateException ex) {
                showDbError(panel, ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a rental to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete this record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // DELETE FROM DATABASE
                    manager.deleteRental(tableModel.getValueAt(row, 0).toString());

                    tentList.remove(row);
                    tableModel.removeRow(row);
                    updateDashboardCount(tentRentalCountLabel, tableModel);
                    JOptionPane.showMessageDialog(panel, "Rental Deleted.");
                } catch (IllegalArgumentException ex) {
                    showError(panel, ex.getMessage());
                } catch (IllegalStateException ex) {
                    showDbError(panel, ex.getMessage());
                }
            }
        });

        // CLEAR FORM ACTION
        btnClear.addActionListener(e -> {
            txtFullName.setText("");
            txtAddress.setText("");
            txtContactNumber.setText("");
            cmbGender.setSelectedIndex(0);
            txtYearOfBirth.setText("");
            txtDate.setText(LocalDate.now().plusDays(1).toString());
            txtQuantity.setText("5");
            txtDays.setText("1");
            table.clearSelection();
        });

        return panel;
    }
}