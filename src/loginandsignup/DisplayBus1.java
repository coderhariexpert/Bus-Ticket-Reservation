package loginandsignup;

import java.awt.Color;
import java.awt.Window;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.JButton;
public class DisplayBus1 extends JFrame {
    private String username;
    private List<SeatButtonListener> selectedSeatsList = new ArrayList<>();
    private List<Integer> selectedSeats = new ArrayList<>(); // Add this line
    private List<Integer> selectedSeatNumbers = new ArrayList<>();

    String SUrl = "jdbc:mysql://localhost:3306/busresv";
    String SUser = "root";
    String SPass = "123";
    private int selectedRow;
    private Home2 home;
    private String date;
    

    public DisplayBus1(String startingpt, String destination, String date, String username, Home2 home) {
        initComponents();
        this.home = home;
        this.date = date;
        this.username = username;
        
        displayData(startingpt, destination, date, username);
    }

private void initComponents() {
    jScrollPane1 = new JScrollPane();
    jTable1 = new JTable();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    jTable1.setModel(new DefaultTableModel(
            new Object[][]{
                    {null, null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null, null},
                    {null, null, null, null, null, null, null, null, null}
            },
            new String[]{
                    "ID", "AC/NON-AC", "CAPACITY", "STARTINGPT", "DESTINATION", "FARE", "DEPARTURE TIME", "ARRIVAL TIME", "Button"
            }
    ) {
        Class[] types = new Class[]{
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, javax.swing.JButton.class
        };
        boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false, false, true
        };

        @Override
        public Class getColumnClass(int columnIndex) {
            return types[columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit[columnIndex];
        }
    });

    JButton goBackButton = new JButton("Go Back to Home");
    goBackButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            goBackToHome();
        }
    });

    // Add a custom cell renderer and editor for the button column
    jTable1.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
    jTable1.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor());

    jScrollPane1.setViewportView(jTable1);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(goBackButton)  // Add the goBackButton here
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(goBackButton)  // Add the goBackButton here
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
    );

    pack();
}

    private void goBackToHome() {
        home.setUser(username); 
        home.setVisible(true);
        home.pack();
        home.setLocationRelativeTo(null);
        this.dispose();
    }
    
    private String getBookedSeatsInfo(String busNumber, String date) throws SQLException {
        String query = "SELECT seat_no FROM booking WHERE bus_no = ? AND travel_date = ?";
        try (Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, Integer.parseInt(busNumber));
            pst.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = pst.executeQuery()) {
                StringBuilder bookedSeats = new StringBuilder();
                while (rs.next()) {
                    bookedSeats.append(rs.getInt("seat_no")).append(",");
                }
                return bookedSeats.toString();
            }
        }
    }
    
    private void updateBookedSeatsColor(String date) {
        try (Connection connection = DriverManager.getConnection(SUrl, SUser, SPass)) {
            String query = "SELECT bus_no, seat_no, Sex FROM booking WHERE travel_date = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDate(1, java.sql.Date.valueOf(date));
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                    while (resultSet.next()) {
                        int busNumber = resultSet.getInt("bus_no");
                        int seatNumber = resultSet.getInt("seat_no");
                        String gender = resultSet.getString("Sex");
    
                        for (int row = 0; row < model.getRowCount(); row++) {
                            if (model.getValueAt(row, 0).equals(busNumber) && model.getValueAt(row, 8) instanceof JButton) {
                                JButton seatButton = (JButton) model.getValueAt(row, 8);
                                String buttonText = seatButton.getText();
                                int extractedSeatNumber = Integer.parseInt(buttonText.substring(buttonText.lastIndexOf(" ") + 1));
    
                                if (extractedSeatNumber == seatNumber) {
                                    // Set color based on gender (case-insensitive)
                                    if ("female".equalsIgnoreCase(gender)) {
                                        seatButton.setBackground(Color.PINK);
                                    } else {
                                        seatButton.setBackground(Color.BLUE); // Change to a color suitable for male passengers
                                    }
    
                                    seatButton.setEnabled(false);  // Disable the button as it's already booked
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error updating booked seats color", e);
        }
    }
    
private void displayData(String startingpt, String destination, String date, String username) {
    try (Connection connection = DriverManager.getConnection(SUrl, SUser, SPass)) {
        String query = "SELECT * FROM bus WHERE startingpt = ? AND destination = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, startingpt);
            preparedStatement.setString(2, destination);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                model.setRowCount(0);
                while (resultSet.next()) {
                    Object[] row = {
                            resultSet.getInt("id"),
                            resultSet.getInt("ac"),
                            resultSet.getInt("capacity"),
                            resultSet.getString("startingpt"),
                            resultSet.getString("destination"),
                            resultSet.getInt("fare"),
                            resultSet.getTime("departure_time"),
                            resultSet.getTime("arrival_time"),
                            new JButton("Book Now")
                    };
                    int busNumber = resultSet.getInt("id");
                    String bookedSeatsInfo = getBookedSeatsInfo(String.valueOf(busNumber), date);
                    JButton seatButton = new JButton("Seat " + busNumber);
                    markBookedSeats(bookedSeatsInfo, seatButton);
                    row[8] = seatButton;
                    model.addRow(row);
                }
                updateBookedSeatsColor(date);
            }
        }
    } catch (SQLException e) {
        handleSQLException("Error retrieving bus data", e);
    }
}

private void markBookedSeats(String bookedSeatsInfo, JButton seatButton) {
    String[] bookedSeatsArray = bookedSeatsInfo.split(",");
    String buttonText = seatButton.getText();
    int seatNumber = Integer.parseInt(buttonText.substring(buttonText.lastIndexOf(" ") + 1));

    if (Arrays.asList(bookedSeatsArray).contains(String.valueOf(seatNumber))) {
        // Check if the booked seat belongs to a female passenger
        String query = "SELECT Sex FROM booking WHERE seat_no = ? AND travel_date = ?";
        try (Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, seatNumber);
            pst.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String gender = rs.getString("Sex");
                    if ("female".equalsIgnoreCase(gender)) {
                        seatButton.setBackground(Color.PINK);
                        seatButton.setEnabled(false);  // Disable the button as it's already booked
                    } else {
                        seatButton.setBackground(Color.RED);
                        seatButton.setEnabled(false);  // Disable the button as it's already booked
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error checking passenger gender", e);
        }
    }
}

// ...



    
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    public class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private final JButton button;
    
        public ButtonEditor() {
            button = new JButton();
            button.addActionListener(this);
        }
    
        @Override
        public Object getCellEditorValue() {
            return null;
        }
    
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRow = row;
            return button;
        }
    
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    openSeatPanel(selectedRow);
                } catch (SQLException ex) {
                    handleSQLException("Error opening seat panel", ex);
                }
            }
            fireEditingStopped();
    
            // Close all other windows except the main DisplayBus1 window
            
        }
    }
    
    
    
    private void displayConfirmationFrame(String busNumber, String date, List<Integer> selectedSeatNumbers) {
        JPanel confirmationPanel = createConfirmationPanel(busNumber, selectedSeatNumbers);
    
        JButton confirmBookingButton = createConfirmBookingButton(busNumber);
        confirmBookingButton.addActionListener(e -> {
            try {
                openPassengerDetailsDialog(busNumber, selectedSeatNumbers);
            } catch (SQLException ex) {
                handleSQLException("Error opening passenger details dialog", ex);
            }
        });
    
        confirmationPanel.add(confirmBookingButton);
    
        JOptionPane.showMessageDialog(this, confirmationPanel, "Confirmation", JOptionPane.PLAIN_MESSAGE);
    }
        private JButton createConfirmBookingButton(String busNumber) {
            JButton confirmBookingButton = new JButton("Confirm Booking");
            confirmBookingButton.addActionListener(e -> {
                //displayConfirmationFrame(busNumber, date, getSelectedSeatNumbers());
            });
            return confirmBookingButton;
        }
        
    private List<Integer> getSelectedSeatNumbers() {
        List<Integer> selectedSeatNumbers = new ArrayList<>();
        for (SeatButtonListener seatButtonListener : selectedSeatsList) {
            if (seatButtonListener.isSelected()) {
                selectedSeatNumbers.add(seatButtonListener.getSeatNumber());
            }
        }
        return selectedSeatNumbers;
    }
    private double calculateGSTAmount(double totalFare) {
        double gstRate = 0.18;
        return totalFare * gstRate;
    }
    
    private JPanel createConfirmationPanel(String busNumber, List<Integer> selectedSeatNumbers) {
        JPanel confirmationPanel = new JPanel();
        confirmationPanel.setLayout(new GridLayout(0, 2));
    
        double totalFare = calculateTotalFare(busNumber, selectedSeatNumbers);
    
        confirmationPanel.add(new JLabel("Selected Seats:"));
        confirmationPanel.add(new JLabel(selectedSeatNumbers.toString()));
    
        double gstAmount = calculateGSTAmount(totalFare);
        confirmationPanel.add(new JLabel("GST (18%):"));
        confirmationPanel.add(new JLabel(String.format("%.2f", gstAmount)));
    
        double totalFareWithGST = totalFare + gstAmount;
        confirmationPanel.add(new JLabel("Total Fare (including GST):"));
        confirmationPanel.add(new JLabel(String.format("%.2f", totalFareWithGST)));
    
        return confirmationPanel;
    }
    
    private double calculateTotalFare(String busNumber, List<Integer> selectedSeatNumbers) {
        double totalFare = 0.0;
        for (int seatNumber : selectedSeatNumbers) {
            try {
                totalFare += getTicketFare(busNumber);
            } catch (SQLException ex) {
                handleSQLException("Error getting ticket fare", ex);
            }
        }
        return totalFare;
    }
    
    // Rest of the methods remain unchanged
    
    private void openSeatPanel(int selectedRow) throws SQLException {
        String busNumber = String.valueOf(jTable1.getValueAt(selectedRow, 0));
        String availableSeatsInfo = String.valueOf(jTable1.getValueAt(selectedRow, 2));
        JPanel seatPanel = new JPanel();
        int capacity = Integer.parseInt(availableSeatsInfo);
        int rows = (int) Math.ceil((double) capacity / 3);
        int columns = 3;
    
        // Create a separate panel just for the seat arrangement
        JPanel seatArrangementPanel = new JPanel();
        seatArrangementPanel.setLayout(new GridLayout(rows, columns, 10, 10));
    
        List<Integer> selectedSeatNumbers = new ArrayList<>();
        for (int i = 1; i <= capacity; i++) {
            final int seatNumber = i; // Declare a final variable
    
            JButton seatButton = new JButton("Seat " + seatNumber);
    
            if (isSeatAlreadyBooked(busNumber, date, seatNumber)) {
                String gender = getBookedPassengerGender(busNumber, date, seatNumber);
    
                if ("female".equalsIgnoreCase(gender)) {
                    seatButton.setBackground(Color.PINK);
                } else {
                    seatButton.setBackground(Color.RED);
                }
    
                seatButton.setEnabled(false);
            } else {
                seatButton.addActionListener(e -> {
                    if (!seatButton.getBackground().equals(Color.RED)) {
                        if (!selectedSeatNumbers.contains(seatNumber)) {
                            selectedSeatNumbers.add(seatNumber);
                            seatButton.setBackground(Color.GREEN);
                        } else {
                            selectedSeatNumbers.remove(Integer.valueOf(seatNumber));
                            seatButton.setBackground(null);
                        }
                    }
                });
            }
    
            seatArrangementPanel.add(seatButton);
        }
    
        seatPanel.add(seatArrangementPanel);  // Add the seat arrangement panel to the main seatPanel
    
        JButton confirmSelectionButton = new JButton("Confirm Selection");
        confirmSelectionButton.addActionListener(e -> {
            displayConfirmationFrame(busNumber, date, selectedSeatNumbers);
            //openPassengerDetailsDialog(busNumber, selectedSeatNumbers);
        });
    
        seatPanel.add(confirmSelectionButton);
    
        JOptionPane.showMessageDialog(this, seatPanel, "Select Seats", JOptionPane.PLAIN_MESSAGE);
    }
    private void openPassengerDetailsDialog(String busNumber, List<Integer> selectedSeatNumbers) throws SQLException {
        JPanel passengerDetailsPanel = new JPanel();
        passengerDetailsPanel.setLayout(new BoxLayout(passengerDetailsPanel, BoxLayout.Y_AXIS));
    
        List<Passenger> passengers = new ArrayList<>();
    
        for (int seatNumber : selectedSeatNumbers) {
            passengerDetailsPanel.add(new JLabel("Enter details for Seat " + seatNumber + ":"));
    
            JTextField nameField = new JTextField();
            JTextField ageField = new JTextField();
            JTextField sexField = new JTextField();
    
            passengerDetailsPanel.add(new JLabel("Name:"));
            passengerDetailsPanel.add(nameField);
            passengerDetailsPanel.add(new JLabel("Age:"));
            passengerDetailsPanel.add(ageField);
            passengerDetailsPanel.add(new JLabel("Male/Female:"));
            passengerDetailsPanel.add(sexField);
    
            // Add some spacing between entries
            passengerDetailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    
            passengers.add(new Passenger(nameField, ageField, sexField, seatNumber));
        }
    
        int result = JOptionPane.showConfirmDialog(this, passengerDetailsPanel, "Enter Passenger Details", JOptionPane.OK_CANCEL_OPTION);
        List<String> passengerNames = new ArrayList<>();
        List<String> passengerAges = new ArrayList<>();
        List<String> passengerSexes = new ArrayList<>();
        if (result == JOptionPane.OK_OPTION) {
            for (Passenger passenger : passengers) {
                String passengerName = passenger.getName().getText();
                String passengerAge = passenger.getAge().getText();
                String passengerSex = passenger.getSex().getText();
    
                // Perform validation and add booking for each passenger
                if (passengerName.isEmpty() || passengerAge.isEmpty() || passengerSex.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all the details.", "Incomplete Details", JOptionPane.WARNING_MESSAGE);
                    return;
                }
    
                passengerNames.add(passengerName);
                passengerAges.add(passengerAge);
                passengerSexes.add(passengerSex);
            }
            double totalFare = calculateTotalFare(busNumber, selectedSeatNumbers);
            List<Integer> seatNumbersList = new ArrayList<>(selectedSeatNumbers);
            this.dispose();
            Payment paymentFrame = new Payment(username, totalFare, busNumber, passengers, date, home);
            paymentFrame.setVisible(true);
        } else {
            System.out.println("Booking canceled");
        }
    }
    
    

    
    
    
    // Helper method to get the gender of the booked passenger
    private String getBookedPassengerGender(String busNumber, String date, int seatNumber) throws SQLException {
        String query = "SELECT Sex FROM booking WHERE bus_no = ? AND travel_date = ? AND seat_no = ?";
        try (Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, Integer.parseInt(busNumber));
            pst.setDate(2, java.sql.Date.valueOf(date));
            pst.setInt(3, seatNumber);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Sex");
                }
            }
        }
        return null;
    }
    
    public class SeatButtonListener implements ActionListener {
        private final String busNumber;
        private final int seatNumber;
        private final JButton seatButton;
        private List<Integer> selectedSeats;
        private boolean selected;
    
        public SeatButtonListener(String busNumber, int seatNumber, JButton seatButton) {
            this.busNumber = busNumber;
            this.seatNumber = seatNumber;
            this.seatButton = seatButton;
            this.selectedSeats = new ArrayList<>();
            this.selected = false;
        }
    
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!seatButton.getBackground().equals(Color.RED)) {
                if (!selectedSeats.contains(seatNumber)) {
                    selectedSeats.add(seatNumber);
                    seatButton.setBackground(Color.GREEN);
                    selected = true;
                } else {
                    selectedSeats.remove(Integer.valueOf(seatNumber));
                    seatButton.setBackground(null);
                    selected = false;
                }
            }
        }
        public void confirmSelectionIfSelected() {
            if (selected) {
                selectedSeatsList.add(this);
            }
        }
    
        public boolean isSelected() {
            return selected;
        }
    
        public int getSeatNumber() {
            return seatNumber;
        }
    
        public void confirmBookingIfSelected() {
            if (selected) {
                //openPassengerDetailsDialog(busNumber, seatNumber);
                //selected = false;
            }
        }
    }
    
    

    private double getTicketFare(String busNumber) throws SQLException {
        String query = "SELECT fare FROM bus WHERE id = ?";
    
        try (Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, Integer.parseInt(busNumber));
    
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("fare");
                }
            }
        }
    
        // Return a default value if the fare is not found
        return 0.0;
    }
    private void addBooking(String busNumber, String passengerName, String passengerAge, String passengerSex, String date, String username, List<Integer> seatNumbers) throws SQLException {
    // Check if the seats are already booked for the specified date and bus
    if (areSeatsAlreadyBooked(busNumber, date, seatNumbers)) {
        JOptionPane.showMessageDialog(this, "Seats are already booked for the specified date and bus.", "Booking Error", JOptionPane.ERROR_MESSAGE);
        return; // Stop the booking process
    }

    // Continue with the booking process
    String pnr = generatePNR(); // Generate PNR for the booking

    String query = "INSERT INTO booking (pnr, passenger_name, bus_no, travel_date, seat_no, ticket_no, fare, departure, arrival, startingpt, destination, email, Sex, pass_age) " +
            "VALUES (?, ?, ?, ?, ?, ?, (SELECT fare FROM bus WHERE id = ?), " +
            "(SELECT departure_time FROM bus WHERE id = ?), " +
            "(SELECT arrival_time FROM bus WHERE id = ?), " +
            "(SELECT startingpt FROM bus WHERE id = ?), " +
            "(SELECT destination FROM bus WHERE id = ?), " +
            "(SELECT email FROM login WHERE email = ?), ?, ?)";

    try (Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
         PreparedStatement pst = con.prepareStatement(query)) {

        int newTicketNumber = getMaxTicketNumber() + 1;
        java.sql.Date sqldate = java.sql.Date.valueOf(date);

        for (int seatNumber : seatNumbers) {
            pst.setString(1, pnr); // Add PNR to the query
            pst.setString(2, passengerName);
            pst.setInt(3, Integer.parseInt(busNumber));
            pst.setDate(4, sqldate);
            pst.setInt(5, seatNumber);
            pst.setInt(6, newTicketNumber);

            int busId = Integer.parseInt(busNumber);
            pst.setInt(7, busId);
            pst.setInt(8, busId);
            pst.setInt(9, busId);
            pst.setInt(10, busId);
            pst.setInt(11, busId);
            pst.setString(12, username);
            pst.setString(13, passengerSex);  // Assuming passengerSex is a String variable
            pst.setString(14, passengerAge);
            pst.addBatch(); // Add the statement to the batch
        }

        // Execute the batch
        int[] affectedRows = pst.executeBatch();

        // Check if all statements were executed successfully
        for (int rows : affectedRows) {
            if (rows <= 0) {
                System.out.println("Error adding booking. No rows affected.");
                return;
            }
        }

        System.out.println("Bookings added successfully. Your Common Ticket No: " + newTicketNumber + ", PNR: " + pnr);
    }
}
private String generatePNR() {
        // Define the characters allowed in the PNR
        String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

        // Set the length of the PNR
        int pnrLength = 10;

        // Use a StringBuilder to build the PNR
        StringBuilder pnrBuilder = new StringBuilder();

        // Create a Random object to generate random indices
        Random random = new Random();

        // Build the PNR by randomly selecting characters from the allowed set
        for (int i = 0; i < pnrLength; i++) {
            int randomIndex = random.nextInt(allowedCharacters.length());
            char randomChar = allowedCharacters.charAt(randomIndex);
            pnrBuilder.append(randomChar);
        }

        // Convert StringBuilder to String and return the PNR
        return pnrBuilder.toString();
    }
    
    
    // Helper method to check if the seats are already booked for the specified date and bus
    private boolean areSeatsAlreadyBooked(String busNumber, String date, List<Integer> seatNumbers) throws SQLException {
        String query = "SELECT COUNT(*) FROM booking WHERE bus_no = ? AND travel_date = ? AND seat_no IN (?)";
        try (Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, Integer.parseInt(busNumber));
            pst.setDate(2, java.sql.Date.valueOf(date));
            pst.setInt(3, seatNumbers.get(0)); // Only check the first seat for simplicity
    
            try (ResultSet rs = pst.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0; // Returns true if at least one seat is already booked
            }
        }
    }
    
    
    // Helper method to check if the seat is already booked for the specified date and bus
    private boolean isSeatAlreadyBooked(String busNumber, String date, int seatNumber) throws SQLException {
        String query = "SELECT COUNT(*) FROM booking WHERE bus_no = ? AND travel_date = ? AND seat_no = ?";
        try (Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, Integer.parseInt(busNumber));
            pst.setDate(2, java.sql.Date.valueOf(date));
            pst.setInt(3, seatNumber);
            try (ResultSet rs = pst.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0; // Returns true if the seat is already booked
            }
        }
    }
    

    private int getMaxTicketNumber() throws SQLException {
        String query = "SELECT MAX(ticket_no) FROM booking";
        try (Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            rs.next();
            return rs.getInt(1);
        }
    }


    private void handleSQLException(String message, SQLException e) {
        JOptionPane.showMessageDialog(this, message + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            String startingpt = "StartingPoint";
            String destination = "Destination";
            String date = "2023-12-01";
            String username = "yourUsername";
             // Replace with your actual method to get the username
            new DisplayBus1(startingpt, destination, date, username,new Home2()).setVisible(true);
        });
    }
}

 class Passenger {
    private JTextField name;
    private JTextField age;
    private JTextField sex;
    private int seatNumber;

    public Passenger(JTextField name, JTextField age, JTextField sex, int seatNumber) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.seatNumber = seatNumber;
    }

    public JTextField getName() {
        return name;
    }

    public JTextField getAge() {
        return age;
    }

    public JTextField getSex() {
        return sex;
    }

    public int getSeatNumber() {
        return seatNumber;
    }
}