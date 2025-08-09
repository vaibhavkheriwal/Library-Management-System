import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Librarian {
    private JFrame frame;
    private DefaultTableModel bookTableModel;
    private JTable bookTable;
    private DefaultTableModel memberTableModel;
    private JTable memberTable;
    private JTextField bookSearchField, memberSearchField;
    private JComboBox<String> bookFilterComboBox, memberFilterComboBox;

    public Librarian() {
        // Create frame
        frame = new JFrame("Library Management System - Librarian");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximized window
        frame.setLayout(new BorderLayout());

        // Top banner
        JLabel titleLabel = new JLabel("Library Management System - Librarian", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(70, 130, 180)); // Steel Blue
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 18));

        // Book Tab
        JPanel bookTab = createBookTab();
        tabbedPane.addTab("Books", bookTab);

        // Member Tab
        JPanel memberTab = createMemberTab();
        tabbedPane.addTab("Members", memberTab);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createBookTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Top panel with search bar, filter, and buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);

        // Search bar
        bookSearchField = new JTextField(30);
        bookSearchField.setFont(new Font("Arial", Font.PLAIN, 16));
        bookSearchField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(bookSearchField);

        // Filter Dropdown
        bookFilterComboBox = new JComboBox<>(new String[] { "All", "Title", "Author", "Genre" });
        bookFilterComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(new JLabel("Filter by:"));
        topPanel.add(bookFilterComboBox);

        // Buttons
        JButton addButton = createButton("Add Book");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddBookPopup();
            }
        });

        JButton editButton = createButton("Edit Book");
        editButton.addActionListener(e -> openEditPopup(frame));

        JButton deleteButton = createButton("Delete Book");
        deleteButton.addActionListener(e -> deleteBook(frame));

        JButton loanButton = createButton("Loan Book");
        loanButton.addActionListener(e -> loanBook(frame));

        JButton returnButton = createButton("Return Book");
        returnButton.addActionListener(e -> returnBook(frame));

        JButton refreshButton = createButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearBookTable(); // Clear existing data in the table
                loadBooksData(); // Reload the books data from books.txt
                filterBooks(); // Reapply any filters
            }
        });

        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(loanButton);
        topPanel.add(returnButton);
        topPanel.add(refreshButton); // Add refresh button

        panel.add(topPanel, BorderLayout.NORTH);

        // Table to display books
        String[] columns = { "Book ID", "Title", "Author", "Genre", "Available" };
        bookTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }
        };
        bookTable = new JTable(bookTableModel);
        bookTable.setFont(new Font("Arial", Font.PLAIN, 16));
        bookTable.setRowHeight(30);
        bookTable.setEnabled(false); // Disable interactions with the table

        // Add MouseListener for double-click to disable further interaction
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double click
                    bookTable.setEnabled(false); // Disable the table after double-click
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(bookTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load book data from books.txt
        loadBooksData();

        // Add key listener for search functionality
        bookSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterBooks();
            }
        });

        // Add action listener for dropdown filter
        bookFilterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterBooks();
            }
        });

        return panel;
    }

    private void clearBookTable() {
        // Remove all rows from the table before reloading the data
        bookTableModel.setRowCount(0);
    }

    private JPanel createMemberTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Top panel with search bar, filter, and buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);

        // Search bar
        memberSearchField = new JTextField(30);
        memberSearchField.setFont(new Font("Arial", Font.PLAIN, 16));
        memberSearchField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(memberSearchField);

        // Filter Dropdown
        memberFilterComboBox = new JComboBox<>(new String[] { "All", "Name", "Course" });
        memberFilterComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(new JLabel("Filter by:"));
        topPanel.add(memberFilterComboBox);

        // Buttons
        JButton addMemberButton = createButton("Add Member");
        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddMember(frame); // Open the Add Member dialog
            }
        });

        JButton editMemberButton = createButton("Edit Member");
        editMemberButton.addActionListener(e -> openEditMemberPopup(frame));

        JButton deleteMemberButton = createButton("Delete Member");
        deleteMemberButton.addActionListener(e -> showDeleteMemberDialog());

        JButton refreshMemberButton = createButton("Refresh");
        refreshMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearMemberTable(); // Clear existing data in the table
                loadMembersData(); // Reload the members data from member.txt
                filterMembers(); // Reapply any filters
            }
        });

        topPanel.add(addMemberButton);
        topPanel.add(editMemberButton);
        topPanel.add(deleteMemberButton);
        topPanel.add(refreshMemberButton); // Add refresh button

        panel.add(topPanel, BorderLayout.NORTH);

        // Table to display members
        String[] memberColumns = { "ID", "Name", "Course", "Password", "Borrowed Book ID" };
        memberTableModel = new DefaultTableModel(memberColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }
        };
        memberTable = new JTable(memberTableModel);
        memberTable.setFont(new Font("Arial", Font.PLAIN, 16));
        memberTable.setRowHeight(30);
        memberTable.setEnabled(false); // Disable interactions with the table

        // Add MouseListener for double-click to disable further interaction
        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double click
                    memberTable.setEnabled(false); // Disable the table after double-click
                }
            }
        });

        JScrollPane memberScrollPane = new JScrollPane(memberTable);
        panel.add(memberScrollPane, BorderLayout.CENTER);

        // Load member data from member.txt
        loadMembersData();

        // Add key listener for search functionality
        memberSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterMembers();
            }
        });

        // Add action listener for dropdown filter
        memberFilterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterMembers();
            }
        });

        return panel;
    }

    private void clearMemberTable() {
        // Remove all rows from the table before reloading the data
        memberTableModel.setRowCount(0);
    }

    private void loadBooksData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<>");
                if (parts.length == 5) {
                    bookTableModel.addRow(new Object[] { parts[0], parts[1], parts[2], parts[3], parts[4] });
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error loading books data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMembersData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("member.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<>");
                if (parts.length == 5) {
                    memberTableModel.addRow(new Object[] { parts[0], parts[1], parts[2], parts[3], parts[4] });
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error loading member data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterBooks() {
        String query = bookSearchField.getText().toLowerCase();
        String filter = bookFilterComboBox.getSelectedItem().toString().toLowerCase();

        // Clear current data
        DefaultTableModel filteredModel = new DefaultTableModel(
                new String[] { "Book ID", "Title", "Author", "Genre", "Available" }, 0);

        // Apply filter logic
        for (int i = 0; i < bookTableModel.getRowCount(); i++) {
            boolean matches = false;

            for (int j = 0; j < bookTableModel.getColumnCount(); j++) {
                String columnName = bookTableModel.getColumnName(j).toLowerCase();

                if (filter.equals("all") || columnName.equals(filter)) {
                    String value = bookTableModel.getValueAt(i, j).toString().toLowerCase();
                    if (value.contains(query)) {
                        matches = true;
                        break;
                    }
                }
            }

            if (matches) {
                filteredModel.addRow(new Object[] {
                        bookTableModel.getValueAt(i, 0),
                        bookTableModel.getValueAt(i, 1),
                        bookTableModel.getValueAt(i, 2),
                        bookTableModel.getValueAt(i, 3),
                        bookTableModel.getValueAt(i, 4)
                });
            }
        }
        bookTable.setModel(filteredModel);
    }

    // Method to show the "Add Book" popup
    private void showAddBookPopup() {
        // Calculate the next Book ID
        int nextBookId = getNextBookId();

        // Create a modal dialog
        JDialog addBookDialog = new JDialog((JFrame) null, "Add New Book", true);
        addBookDialog.setSize(450, 350);
        addBookDialog.setLayout(new BorderLayout());
        addBookDialog.setLocationRelativeTo(null); // Center the dialog on the screen

        // Panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        inputPanel.setBackground(Color.WHITE);

        // Input fields with labels
        inputPanel.add(new JLabel("Book ID:", JLabel.RIGHT));
        JLabel bookIdLabel = new JLabel(String.valueOf(nextBookId)); // Show the generated Book ID
        bookIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(bookIdLabel);

        inputPanel.add(new JLabel("Title:", JLabel.RIGHT));
        JTextField titleField = new JTextField();
        titleField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Author:", JLabel.RIGHT));
        JTextField authorField = new JTextField();
        authorField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(authorField);

        inputPanel.add(new JLabel("Genre:", JLabel.RIGHT));
        String[] genres = {
                "Fiction", "Dystopian", "Classic", "Romance", "Adventure",
                "Historical", "Epic", "Fantasy", "Psychological", "Historical Fiction",
                "Satire", "Post-apocalyptic", "Horror", "Science Fiction",
                "Crime Fiction", "Thriller", "Young Adult", "Coming-of-age",
                "Literary Fiction", "Modernist"
        };
        JComboBox<String> genreComboBox = new JComboBox<>(genres);
        genreComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(genreComboBox);

        inputPanel.add(new JLabel("Available:", JLabel.RIGHT));
        JComboBox<String> availableComboBox = new JComboBox<>(new String[] { "yes", "no" });
        availableComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(availableComboBox);

        addBookDialog.add(inputPanel, BorderLayout.CENTER);

        // Panel for action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        // Action listener for save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String genre = (String) genreComboBox.getSelectedItem();
                String available = (String) availableComboBox.getSelectedItem();

                // Validate fields
                if (title.isEmpty() || author.isEmpty()) {
                    JOptionPane.showMessageDialog(addBookDialog,
                            "Please fill all required fields!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Add the new book data to the database and table
                String newBook = nextBookId + "," + title + "," + author + "," + genre + "," + available;
                saveBookToFile(newBook); // Save to books.txt
                bookTableModel.addRow(new Object[] { nextBookId, title, author, genre, available });

                addBookDialog.dispose();
            }
        });

        // Action listener for cancel button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBookDialog.dispose();
            }
        });

        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        addBookDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set the dialog visible
        addBookDialog.setVisible(true);
    }

    // Method to get the next Book ID from books.txt
    private int getNextBookId() {
        int maxId = 0; // Default for an empty database
        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookData = line.split("<>"); // Split by "<>"
                if (bookData.length > 0) { // Ensure at least one column exists
                    try {
                        int currentId = Integer.parseInt(bookData[0].trim()); // Parse Book ID
                        maxId = Math.max(maxId, currentId); // Track the maximum ID
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid Book ID in line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading books.txt: " + e.getMessage());
        }
        return maxId + 1; // Return the next Book ID
    }

    // Method to save a new book record to books.txt
    private void saveBookToFile(String bookRecord) {
        // Replace commas with "<>" in the bookRecord
        String formattedRecord = bookRecord.replace(",", "<>");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("books.txt", true))) {
            writer.write(formattedRecord); // Write the formatted record to the file
            writer.newLine(); // Add a newline after the record
        } catch (IOException ex) {
            ex.printStackTrace(); // Log any errors
        }
    }

    private void filterMembers() {
        String query = memberSearchField.getText().toLowerCase();
        String filter = memberFilterComboBox.getSelectedItem().toString().toLowerCase();

        // Clear current data
        DefaultTableModel filteredModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Course", "Password", "Borrowed Book ID" }, 0);

        // Apply filter logic
        for (int i = 0; i < memberTableModel.getRowCount(); i++) {
            boolean matches = false;

            for (int j = 0; j < memberTableModel.getColumnCount(); j++) {
                String columnName = memberTableModel.getColumnName(j).toLowerCase();

                if (filter.equals("all") || columnName.equals(filter)) {
                    String value = memberTableModel.getValueAt(i, j).toString().toLowerCase();
                    if (value.contains(query)) {
                        matches = true;
                        break;
                    }
                }
            }

            if (matches) {
                filteredModel.addRow(new Object[] {
                        memberTableModel.getValueAt(i, 0),
                        memberTableModel.getValueAt(i, 1),
                        memberTableModel.getValueAt(i, 2),
                        memberTableModel.getValueAt(i, 3),
                        memberTableModel.getValueAt(i, 4)
                });
            }
        }
        memberTable.setModel(filteredModel);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    // Open the first popup to take the book ID
    public static void openEditPopup(JFrame parent) {
        String bookId = JOptionPane.showInputDialog(parent, "Enter Book ID:");

        if (bookId != null && !bookId.isEmpty()) {
            try {
                String bookData = findBookById(bookId);
                if (bookData != null) {
                    openEditDetailsPopup(parent, bookData, bookId);
                } else {
                    JOptionPane.showMessageDialog(parent, "Book ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Error reading the database.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Open the second popup to edit book details
    public static void openEditDetailsPopup(JFrame parent, String bookData, String bookId) throws IOException {
        String[] bookDetails = bookData.split("<>");

        // Available genres
        String[] genres = {
                "Fiction", "Dystopian", "Classic", "Romance", "Adventure", "Historical",
                "Epic", "Fantasy", "Psychological", "Historical Fiction", "Satire",
                "Post-apocalyptic", "Horror", "Science Fiction", "Crime Fiction",
                "Thriller", "Young Adult", "Coming-of-age", "Literary Fiction", "Modernist"
        };

        // Preselect genre in the dropdown
        JComboBox<String> genreDropdown = new JComboBox<>(genres);
        genreDropdown.setSelectedItem(bookDetails[3]);

        JTextField titleField = new JTextField(bookDetails[1]);
        JTextField authorField = new JTextField(bookDetails[2]);
        JComboBox<String> availabilityDropdown = new JComboBox<>(new String[] { "yes", "no" });
        availabilityDropdown.setSelectedItem(bookDetails[4]);

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Book ID:"));
        panel.add(new JLabel(bookId)); // Book ID as a label
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Genre:"));
        panel.add(genreDropdown); // Genre as a dropdown
        panel.add(new JLabel("Availability:"));
        panel.add(availabilityDropdown);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Edit Book Details", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String updatedBookData = bookId + "<>" + titleField.getText() + "<>" + authorField.getText() + "<>" +
                    genreDropdown.getSelectedItem() + "<>" + availabilityDropdown.getSelectedItem();
            updateBookInDatabase(bookId, updatedBookData);
            JOptionPane.showMessageDialog(parent, "Book details updated successfully!");
        }
    }

    // Find a book by its ID
    public static String findBookById(String bookId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(bookId + "<>")) {
                    return line;
                }
            }
        }
        return null;
    }

    // Update the book details in the database
    public static void updateBookInDatabase(String bookId, String updatedBookData) throws IOException {
        File inputFile = new File("books.txt");
        File tempFile = new File("books_temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(bookId + "<>")) {
                    writer.write(updatedBookData);
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new IOException("Failed to update the book database.");
        }
    }

    // Delete a book by its ID
    public static void deleteBook(JFrame parent) {
        String bookId = JOptionPane.showInputDialog(parent, "Enter Book ID to Delete:");

        if (bookId != null && !bookId.isEmpty()) {
            try {
                boolean deleted = deleteBookFromDatabase(bookId);
                if (deleted) {
                    JOptionPane.showMessageDialog(parent, "Book deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(parent, "Book ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Error updating the database.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Delete a book by its ID in the database
    public static boolean deleteBookFromDatabase(String bookId) throws IOException {
        File inputFile = new File("books.txt");
        File tempFile = new File("books_temp.txt");
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(bookId + "<>")) {
                    found = true; // Skip writing the book to the temp file
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }

        if (found) {
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                throw new IOException("Failed to update the book database.");
            }
        } else {
            tempFile.delete(); // Clean up temporary file
        }

        return found;
    }

    // Loan a book to a member
    public static void loanBook(JFrame parent) {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField bookIdField = new JTextField();
        JTextField memberIdField = new JTextField();

        panel.add(new JLabel("Book ID:"));
        panel.add(bookIdField);
        panel.add(new JLabel("Member ID:"));
        panel.add(memberIdField);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Loan Book", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String bookId = bookIdField.getText();
            String memberId = memberIdField.getText();

            if (bookId.isEmpty() || memberId.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Both Book ID and Member ID are required.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String bookData = findBookById(bookId);
                String memberData = findMemberById(memberId);

                if (bookData == null) {
                    JOptionPane.showMessageDialog(parent, "Book ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (memberData == null) {
                    JOptionPane.showMessageDialog(parent, "Member ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] bookDetails = bookData.split("<>");
                String[] memberDetails = memberData.split("<>");

                if (!bookDetails[4].equalsIgnoreCase("yes")) {
                    JOptionPane.showMessageDialog(parent, "Book is not available for loan.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!memberDetails[4].equalsIgnoreCase("N/A")) {
                    JOptionPane.showMessageDialog(parent, "Member already has a book loaned.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update the member and book databases
                String updatedMemberData = memberDetails[0] + "<>" + memberDetails[1] + "<>" + memberDetails[2] + "<>" +
                        memberDetails[3] + "<>" + bookId;
                String updatedBookData = bookDetails[0] + "<>" + bookDetails[1] + "<>" + bookDetails[2] + "<>" +
                        bookDetails[3] + "<>no";

                updateMemberDatabase(memberId, updatedMemberData);
                updateBookDatabase(bookId, updatedBookData);
                JOptionPane.showMessageDialog(parent, "Book loaned successfully!");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Error processing loan request.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Update the book database to mark it as loaned
    public static void updateBookDatabase(String bookId, String updatedBookData) throws IOException {
        File inputFile = new File("books.txt");
        File tempFile = new File("books_temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(bookId + "<>")) {
                    writer.write(updatedBookData);
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new IOException("Failed to update the book database.");
        }
    }

    // Check if a book exists
    public static boolean checkBookExists(String bookId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(bookId + "<>")) {
                    return true;
                }
            }
        }
        return false;
    }

    // Find a member by their ID
    public static String findMemberById(String memberId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("member.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(memberId + "<>")) {
                    return line;
                }
            }
        }
        return null;
    }

    // Update the member database with new loan data
    public static void updateMemberDatabase(String memberId, String updatedMemberData) throws IOException {
        File inputFile = new File("member.txt");
        File tempFile = new File("member_temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(memberId + "<>")) {
                    writer.write(updatedMemberData);
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new IOException("Failed to update the member database.");
        }
    }

    // Return a book from a member
    public static void returnBook(JFrame parent) {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField bookIdField = new JTextField();
        JTextField memberIdField = new JTextField();

        panel.add(new JLabel("Book ID:"));
        panel.add(bookIdField);
        panel.add(new JLabel("Member ID:"));
        panel.add(memberIdField);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Return Book", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String bookId = bookIdField.getText();
            String memberId = memberIdField.getText();

            if (bookId.isEmpty() || memberId.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Both Book ID and Member ID are required.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String bookData = findBookById(bookId);
                String memberData = findMemberById(memberId);

                if (bookData == null) {
                    JOptionPane.showMessageDialog(parent, "Book ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (memberData == null) {
                    JOptionPane.showMessageDialog(parent, "Member ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] bookDetails = bookData.split("<>");
                String[] memberDetails = memberData.split("<>");

                if (!bookDetails[4].equalsIgnoreCase("no")) {
                    JOptionPane.showMessageDialog(parent, "This book is already available and cannot be returned.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!memberDetails[4].equals(bookId)) {
                    JOptionPane.showMessageDialog(parent, "This member has not borrowed this book.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update the member and book databases
                String updatedMemberData = memberDetails[0] + "<>" + memberDetails[1] + "<>" + memberDetails[2] + "<>" +
                        memberDetails[3] + "<>N/A";
                String updatedBookData = bookDetails[0] + "<>" + bookDetails[1] + "<>" + bookDetails[2] + "<>" +
                        bookDetails[3] + "<>yes";

                updateMemberDatabase(memberId, updatedMemberData);
                updateBookDatabase(bookId, updatedBookData);
                JOptionPane.showMessageDialog(parent, "Book returned successfully!");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Error processing return request.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public class AddMember {

        private JTextField nameField, passwordField;
        private JComboBox<String> courseComboBox;
        private JButton saveButton, cancelButton;

        public AddMember(JFrame parentFrame) {
            // Get the next Member ID
            int nextMemberId = getNextMemberId();

            // Create a modal dialog
            JDialog addMemberDialog = new JDialog(parentFrame, "Add New Member", true);
            addMemberDialog.setSize(450, 350);
            addMemberDialog.setLayout(new BorderLayout());
            addMemberDialog.setLocationRelativeTo(parentFrame); // Center the dialog on the screen

            // Panel for input fields
            JPanel inputPanel = new JPanel(new GridLayout(5, 2, 15, 15));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
            inputPanel.setBackground(Color.WHITE);

            // Member ID
            inputPanel.add(new JLabel("Member ID:", JLabel.RIGHT));
            JLabel memberIdLabel = new JLabel(String.valueOf(nextMemberId)); // Show the generated Member ID
            memberIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
            inputPanel.add(memberIdLabel);

            // Name field
            inputPanel.add(new JLabel("Name:", JLabel.RIGHT));
            nameField = new JTextField();
            nameField.setFont(new Font("Arial", Font.PLAIN, 14));
            inputPanel.add(nameField);

            // Course dropdown
            inputPanel.add(new JLabel("Course:", JLabel.RIGHT));
            String[] courses = { "BCA", "MCA" };
            courseComboBox = new JComboBox<>(courses);
            courseComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
            inputPanel.add(courseComboBox);

            // Password field (use JTextField for visible text)
            inputPanel.add(new JLabel("Password:", JLabel.RIGHT));
            passwordField = new JTextField(); // Change from JPasswordField to JTextField
            passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
            inputPanel.add(passwordField);

            // Book ID (default to N/A)
            inputPanel.add(new JLabel("Book ID:", JLabel.RIGHT));
            JLabel bookIdLabel = new JLabel("N/A"); // Default Book ID
            bookIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
            inputPanel.add(bookIdLabel);

            addMemberDialog.add(inputPanel, BorderLayout.CENTER);

            // Panel for action buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
            buttonPanel.setBackground(Color.WHITE);

            saveButton = new JButton("Save");
            cancelButton = new JButton("Cancel");

            // Action listener for save button
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = nameField.getText().trim();
                    String course = (String) courseComboBox.getSelectedItem();
                    String password = passwordField.getText().trim();
                    int memberId = nextMemberId; // Use the generated Member ID
                    String bookId = "N/A"; // Default Book ID

                    // Validate fields
                    if (name.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(addMemberDialog,
                                "Please fill all required fields!", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Add the new member data to the database (member.txt file)
                    String newMember = memberId + "<>" + name + "<>" + course + "<>" + password + "<>" + bookId;
                    saveMemberToFile(newMember); // Save to member.txt

                    // Close the dialog
                    addMemberDialog.dispose();
                }
            });

            // Action listener for cancel button
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addMemberDialog.dispose();
                }
            });

            saveButton.setFont(new Font("Arial", Font.BOLD, 14));
            cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            addMemberDialog.add(buttonPanel, BorderLayout.SOUTH);

            // Set the dialog visible
            addMemberDialog.setVisible(true);
        }

        // Method to get the next available member ID by reading the member database
        private int getNextMemberId() {
            File file = new File("member.txt");
            int nextId = 1;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] memberDetails = line.split("<>");
                    int currentId = Integer.parseInt(memberDetails[0]);
                    if (currentId >= nextId) {
                        nextId = currentId + 1;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return nextId;
        }

        // Method to save the new member to the member database (member.txt file)
        private void saveMemberToFile(String newMember) {
            File file = new File("member.txt");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(newMember);
                writer.newLine();
                JOptionPane.showMessageDialog(null, "Member added successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void openEditMemberPopup(JFrame parent) {
        // Step 1: Input Member ID
        String memberId = JOptionPane.showInputDialog(parent, "Enter Member ID:");

        if (memberId != null && !memberId.isEmpty()) {
            try {
                // Step 2: Check if Member Exists
                String memberData = findMemberById(memberId); // Function to retrieve member data
                if (memberData != null) {
                    openEditMemberDetailsPopup(parent, memberData, memberId); // Open detailed edit popup
                } else {
                    JOptionPane.showMessageDialog(parent, "Member ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Error reading the database.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void openEditMemberDetailsPopup(JFrame parent, String memberData, String memberId) {
        // Parse the member data
        String[] details = memberData.split("<>");
        String currentName = details[1];
        String currentCourse = details[2];
        String currentPassword = details[3];
        String currentBookId = details[4];

        // Dropdown for course options
        JComboBox<String> courseDropdown = new JComboBox<>(new String[] { "BCA", "MCA", "BBA", "MBA" });
        courseDropdown.setSelectedItem(currentCourse);

        // Input fields
        JTextField nameField = new JTextField(currentName);
        JTextField passwordField = new JTextField(currentPassword);

        // Panel layout
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Member ID:"));
        panel.add(new JLabel(memberId)); // Non-editable
        panel.add(new JLabel("Name:"));
        panel.add(nameField); // Editable field
        panel.add(new JLabel("Course:"));
        panel.add(courseDropdown); // Dropdown
        panel.add(new JLabel("Password:"));
        panel.add(passwordField); // Editable field
        panel.add(new JLabel("Book ID:"));
        panel.add(new JLabel(currentBookId)); // Non-editable

        // Show confirm dialog
        int result = JOptionPane.showConfirmDialog(parent, panel, "Edit Member Details", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Directly call updateMemberDetails without using updatedMemberData
                updateMemberDetails(memberId, nameField.getText().trim(), courseDropdown.getSelectedItem().toString(),
                        passwordField.getText().trim());
                JOptionPane.showMessageDialog(parent, "Member details updated successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Error updating member details.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void updateMemberDetails(String memberId, String name, String course, String password)
            throws IOException {
        File file = new File("member.txt");
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<>");
                if (parts[0].equals(memberId)) {
                    lines.add(memberId + "<>" + name + "<>" + course + "<>" + password + "<>" + parts[4]);
                } else {
                    lines.add(line);
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        }
    }

    // Show dialog to input Member ID and attempt to delete
    public static void showDeleteMemberDialog() {
        // Ask user for Member ID
        String memberId = JOptionPane.showInputDialog(null, "Enter Member ID to delete:");

        if (memberId != null && !memberId.isEmpty()) {
            try {
                // Check if the member exists and delete
                if (deleteMemberFromDatabase(memberId)) {
                    JOptionPane.showMessageDialog(null, "Member deleted successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Member not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error processing the request.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to delete a member from the database (text file)
    public static boolean deleteMemberFromDatabase(String memberId) throws IOException {
        File file = new File("member.txt"); // Make sure this path is correct
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder newContent = new StringBuilder();
        String line;
        boolean memberFound = false;

        while ((line = reader.readLine()) != null) {
            String[] memberDetails = line.split("<>");
            if (memberDetails[0].equals(memberId)) { // If member ID matches, skip this line
                memberFound = true; // Set flag to true if member is found
                continue; // Skip the line for this member
            }
            newContent.append(line).append("\n");
        }
        reader.close();

        if (memberFound) {
            // Write the new content (without the deleted member) back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(newContent.toString());
            writer.close();
            return true; // Member deleted
        }
        return false; // Member not found
    }
}