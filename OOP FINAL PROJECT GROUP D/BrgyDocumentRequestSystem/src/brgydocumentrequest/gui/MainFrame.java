package brgydocumentrequest.gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final String LOGIN_CARD = "LOGIN";
    private static final String DASHBOARD_CARD = "DASHBOARD";

    private CardLayout cardLayout;
    private JPanel container;

    public MainFrame() {
        setTitle("Barangay Document Request System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // CardLayout shows only one panel at a time (login OR dashboard)
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Logout button of the dashboard
        DashboardPanel dashboardPanel = new DashboardPanel(() -> confirmLogout());
        // Successful login, go to the dashboard
        LoginPanel loginPanel = new LoginPanel(() -> showDashboard());

        container.add(loginPanel, LOGIN_CARD);
        container.add(dashboardPanel, DASHBOARD_CARD);
        add(container);
        showLogin();
    }

    private void confirmLogout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            showLogin();
        }
    }

    private void showLogin() {
        cardLayout.show(container, LOGIN_CARD);
    }

    private void showDashboard() {
        cardLayout.show(container, DASHBOARD_CARD);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}