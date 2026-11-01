import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel{
    private JTextField usernameField;
    private JPasswordField  passwordField;
    private JButton loginButton;
    private JLabel errorLabel;
    private LoginListener loginListener;


    private final String username = "un";
    private final String password = "1234";


    public interface LoginListener {
        void onLoginSuccess();
    }
    public LoginPanel(LoginListener listener) {
        this.loginListener = listener;
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //TITLE
        JLabel titleLabel = new JLabel("Barangay Document Request System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(33, 37, 41));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        //USERNAME
        gbc.gridwidth = 1;
        gbc.gridy=1;
        gbc.gridx = 0;
        add(new JLabel("Username: "), gbc);

        usernameField = new JTextField(15);
        gbc.gridx=1;
        add(usernameField, gbc);

        // PASSWORD
        gbc.gridy=2;
        gbc.gridx=0;
        add(new JLabel("Password: "), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        //Error Label
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        gbc.gridy=3;
        gbc.gridx=0;
        gbc.gridwidth= 2;
        add(errorLabel, gbc);

        //LOGIN BUTTON
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(40, 116, 240));
        loginButton.setForeground(Color.WHITE);

        gbc.gridy = 4;
        add(loginButton, gbc);

        //Action Listener
        loginButton.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();


        // Check Credentials
        if (username.equals(this.username) && password.equals(this.password)) {
            errorLabel.setText(" ");
            usernameField.setText(" ");
            passwordField.setText(" ");

            if(loginListener != null) {
                loginListener.onLoginSuccess();
            }
            } else {
                errorLabel.setText(" Invalid Username or Password");
            }

    }
}
