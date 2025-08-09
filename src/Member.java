import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Member {
    public Member(String memberUsername) {
        // Setup Member Dashboard Frame
        JFrame memberFrame = new JFrame("Member Dashboard");
        memberFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        memberFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Open in maximized mode
        memberFrame.setLayout(new BorderLayout());

        // Top banner
        JLabel titleLabel = new JLabel("Member Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(70, 130, 180)); // Steel Blue
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        memberFrame.add(titleLabel, BorderLayout.NORTH);

        // Center panel for member details
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fetch and display member details
        String[] memberDetails = getMemberDetails(memberUsername);

        if (memberDetails != null) {
            JLabel welcomeLabel = new JLabel("Welcome, " + memberDetails[1]);
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            contentPanel.add(welcomeLabel, gbc);

            JLabel usernameLabel = new JLabel("Your Username And Roll Number → " + memberDetails[0]);
            usernameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            gbc.gridy = 1;
            contentPanel.add(usernameLabel, gbc);

            // Correctly show the course now
            JLabel courseLabel = new JLabel("Your Course → " + memberDetails[2]); // Corrected to show course, not
                                                                                  // password
            courseLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            gbc.gridy = 2;
            contentPanel.add(courseLabel, gbc);

            // Borrowed Book Details
            String borrowedBookID = memberDetails[4]; // Book ID that the member has borrowed

            if (!borrowedBookID.equals("N/A")) {
                // Book has been borrowed
                String[] bookDetails = getBookDetails(borrowedBookID);
                if (bookDetails != null) {
                    JLabel borrowedBookLabel = new JLabel("Borrowed Book → Yes");
                    borrowedBookLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                    gbc.gridy = 3;
                    contentPanel.add(borrowedBookLabel, gbc);

                    JLabel bookIDLabel = new JLabel("Book ID → " + borrowedBookID);
                    bookIDLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                    gbc.gridy = 4;
                    contentPanel.add(bookIDLabel, gbc);

                    JLabel bookNameLabel = new JLabel("Book Name → " + bookDetails[1]);
                    bookNameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                    gbc.gridy = 5;
                    contentPanel.add(bookNameLabel, gbc);

                    JLabel authorLabel = new JLabel("Author → " + bookDetails[2]);
                    authorLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                    gbc.gridy = 6;
                    contentPanel.add(authorLabel, gbc);

                    JLabel genreLabel = new JLabel("Book Genre → " + bookDetails[3]);
                    genreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                    gbc.gridy = 7;
                    contentPanel.add(genreLabel, gbc);
                }
            } else {
                // No borrowed book
                JLabel borrowedBookLabel = new JLabel("Borrowed Book → No");
                borrowedBookLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                gbc.gridy = 3;
                contentPanel.add(borrowedBookLabel, gbc);
            }

            // Reset Password Button
            JButton resetPasswordButton = new JButton("Reset Password");
            resetPasswordButton.setFont(new Font("Arial", Font.BOLD, 18));
            resetPasswordButton.setBackground(new Color(70, 130, 180)); // Steel Blue
            resetPasswordButton.setForeground(Color.WHITE);
            resetPasswordButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Pop-up to reset password
                    resetPasswordPopup(memberUsername);
                }
            });
            gbc.gridy = 8;
            contentPanel.add(resetPasswordButton, gbc);
        } else {
            JLabel errorLabel = new JLabel("Member Details Not Found!");
            errorLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            gbc.gridy = 1;
            contentPanel.add(errorLabel, gbc);
        }

        memberFrame.add(contentPanel, BorderLayout.CENTER);

        // Set the frame visible
        memberFrame.setVisible(true);
    }

    // Method to fetch member details from the file
    private String[] getMemberDetails(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader("member.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("<>");
                if (parts[0].equals(username)) {
                    return parts; // Return member details (ID, name, course, password, borrowedBookID)
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if member not found
    }

    // Method to fetch book details based on the borrowed book ID
    private String[] getBookDetails(String bookID) {
        try (BufferedReader br = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("<>");
                if (parts[0].equals(bookID)) {
                    return parts; // Return book details (ID, name, author, genre)
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if book not found
    }

    // Method to reset password through a pop-up
    private void resetPasswordPopup(String username) {
        // Create a dialog to reset password
        JDialog resetDialog = new JDialog();
        resetDialog.setTitle("Reset Password");
        resetDialog.setSize(300, 180); // Simple size for the pop-up
        resetDialog.setLayout(new GridBagLayout());
        resetDialog.setLocationRelativeTo(null); // Center the dialog

        // Panel for simple password fields
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10)); // 3 rows, 2 columns layout

        JLabel currentPasswordLabel = new JLabel("Current Password");
        JTextField currentPasswordField = new JTextField(); // Visible password field
        JLabel newPasswordLabel = new JLabel("New Password");
        JTextField newPasswordField = new JTextField(); // Visible password field

        panel.add(currentPasswordLabel);
        panel.add(currentPasswordField);
        panel.add(newPasswordLabel);
        panel.add(newPasswordField);

        // Save Button
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentPass = currentPasswordField.getText();
                String newPass = newPasswordField.getText();

                // Check current password and update if it matches
                String[] memberDetails = getMemberDetails(username);
                if (memberDetails != null && memberDetails[3].equals(currentPass)) {
                    // Update password in the file
                    updatePasswordInFile(username, newPass);
                    JOptionPane.showMessageDialog(resetDialog, "Password updated successfully!");
                    resetDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(resetDialog, "Current password is incorrect!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Cancel Button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(255, 69, 0)); // Red color for Cancel button
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetDialog.dispose(); // Close the dialog without saving
            }
        });

        // Adding buttons to the panel
        panel.add(saveButton);
        panel.add(cancelButton);

        resetDialog.add(panel);
        resetDialog.setVisible(true);
    }

    // Method to update password in the file
    private void updatePasswordInFile(String username, String newPassword) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("member.txt"));
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("<>");
                if (parts[0].equals(username)) {
                    parts[3] = newPassword; // Update the password
                }
                fileContent.append(String.join("<>", parts)).append("\n");
            }
            br.close();

            // Write updated content back to the file
            BufferedWriter bw = new BufferedWriter(new FileWriter("member.txt"));
            bw.write(fileContent.toString());
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}