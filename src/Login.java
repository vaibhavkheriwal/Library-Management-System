import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Login {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JComboBox<String> userTypeDropdown;

    public Login() {
        // Create frame
        frame = new JFrame("Library Management System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Open in maximized mode
        frame.setLayout(new BorderLayout());

        // Top banner
        JLabel titleLabel = new JLabel("Library Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(70, 130, 180)); // Steel Blue
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Center panel for form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameField, gbc);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        // User type dropdown
        JLabel userTypeLabel = new JLabel("User Type:");
        userTypeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        String[] userTypes = { "Member", "Librarian" };
        userTypeDropdown = new JComboBox<>(userTypes);
        userTypeDropdown.setFont(new Font("Arial", Font.PLAIN, 16));
        userTypeDropdown.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(userTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(userTypeDropdown, gbc);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setBackground(new Color(70, 130, 180)); // Steel Blue
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);

        frame.add(formPanel, BorderLayout.CENTER);

        // Action listener for login
        loginButton.addActionListener(e -> attemptLogin());

        frame.setVisible(true);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String userType = (String) userTypeDropdown.getSelectedItem();

        if (userType.equals("Member")) {
            if (validateMember(username, password)) {
                JOptionPane.showMessageDialog(frame, "Member login successful!");
                openMemberPage();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid member credentials!");
            }
        } else if (userType.equals("Librarian")) {
            if (validateLibrarian(username, password)) {
                JOptionPane.showMessageDialog(frame, "Librarian login successful!");
                openLibrarianPage();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid librarian credentials!");
            }
        }
    }

    private boolean validateMember(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader("member.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("<>");
                if (parts[0].equals(username) && parts[3].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean validateLibrarian(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader("librarian.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("<>");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void openMemberPage() {
        String memberUsername = usernameField.getText(); // Get the member username
        new Member(memberUsername); // Pass the username to the Member class
    }

    private void openLibrarianPage() {
        new Librarian();
    }

    public static void main(String[] args) {
        new Login();
    }
}
