package loginandsignup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

public class Payment extends javax.swing.JFrame {

    private List<Passenger> passengers;
    private String username;
    private double fare;
    private String busNumber;
    private String date;
    private Home2 home;

    /**
     * Creates new form Payment
     */
    public Payment(String username, double fare, String busNumber, List<Passenger> passengers, String date, Home2 home) {
        this.home = home;
        this.username = username;
        this.fare = fare;
        this.busNumber = busNumber;
        this.passengers = passengers;
        this.date = date;
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        scrollingLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("Payment Gateway");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Payment Mode");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Wallet", "Debit Card", "Credit Card" }));

        jButton1.setText("Submit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(256, 256, 256)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(238, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(88, 88, 88))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(scrollingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(39, 39, 39))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(jComboBox1))
                .addGap(53, 53, 53)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 104, Short.MAX_VALUE)
                .addComponent(scrollingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String selectedPaymentMode = jComboBox1.getSelectedItem().toString();

        switch (selectedPaymentMode) {
            case "Wallet":
                double walletBalance = getWalletBalance(username);
                if (walletBalance >= fare) {
                    deductFromWallet(username, fare);
                    JOptionPane.showMessageDialog(this, "Payment successful! Fare deducted from wallet.", "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient funds in the wallet. Please choose another payment mode.", "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                }
                break;

            case "Debit Card":
            case "Credit Card":
                // Handle debit/credit card payment
                break;

            default:
                break;
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void deductFromWallet(String username, double amountToDeduct) {
        String url = "jdbc:mysql://localhost:3306/busresv";
        String userName = "root";
        String passWord = "123";

        try (Connection connection = DriverManager.getConnection(url, userName, passWord)) {
            String updateQuery = "UPDATE wallet SET amount = amount - ? WHERE username = ? AND amount >= ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setDouble(1, amountToDeduct);
                updateStatement.setString(2, username);
                updateStatement.setDouble(3, amountToDeduct);

                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Fare deducted from wallet.");
                    addBooking(); // Call addBooking for each passenger
                    goBackToHome();
                } else {
                    System.out.println("Failed to deduct fare from wallet. Insufficient funds or other issue.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void goBackToHome() {
        home.setUser(username);
        home.setVisible(true);
        home.pack();
        home.setLocationRelativeTo(null);
        this.dispose();
    }

    private void addBooking() {
        String url = "jdbc:mysql://localhost:3306/busresv";
        String userName = "root";
        String passWord = "123";

        String pnr = generatePNR();

        String query = "INSERT INTO booking (pnr, passenger_name, bus_no, travel_date, seat_no, ticket_no, fare, departure, arrival, startingpt, destination, email, Sex, pass_age) " +
                "VALUES (?, ?, ?, ?, ?, ?, (SELECT fare FROM bus WHERE id = ?), " +
                "(SELECT departure_time FROM bus WHERE id = ?), " +
                "(SELECT arrival_time FROM bus WHERE id = ?), " +
                "(SELECT startingpt FROM bus WHERE id = ?), " +
                "(SELECT destination FROM bus WHERE id = ?), " +
                "(SELECT email FROM login WHERE email = ?), ?, ?)";

        try (Connection con = DriverManager.getConnection(url, userName, passWord);
             PreparedStatement pst = con.prepareStatement(query)) {

            int newTicketNumber = getMaxTicketNumber() + 1;
            java.sql.Date sqldate = java.sql.Date.valueOf(date);

            for (Passenger passenger : passengers) {
                pst.setString(1, pnr);
                pst.setString(2, passenger.getName().getText());
                pst.setInt(3, Integer.parseInt(busNumber));
                pst.setDate(4, sqldate);
                pst.setInt(5, passenger.getSeatNumber());
                pst.setInt(6, newTicketNumber);

                int busId = Integer.parseInt(busNumber);
                pst.setInt(7, busId);
                pst.setInt(8, busId);
                pst.setInt(9, busId);
                pst.setInt(10, busId);
                pst.setInt(11, busId);
                pst.setString(12, username);
                pst.setString(14, passenger.getAge().getText());
pst.setString(13, passenger.getSex().getText());
                pst.addBatch();
            }

            int[] affectedRows = pst.executeBatch();

            for (int rows : affectedRows) {
                if (rows <= 0) {
                    System.out.println("Error adding booking. No rows affected.");
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Bookings confirmed successfully!", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Bookings added successfully. Your Common Ticket No: " + newTicketNumber + ", PNR: " + pnr);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getMaxTicketNumber() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/busresv";
        String userName = "root";
        String passWord = "123";
        String query = "SELECT MAX(ticket_no) FROM booking";
        try (Connection con = DriverManager.getConnection(url, userName, passWord);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private double getWalletBalance(String username) {
        double balance = 0.0;

        String url = "jdbc:mysql://localhost:3306/busresv";
        String userName = "root";
        String passWord = "123";

        try (Connection connection = DriverManager.getConnection(url, userName, passWord)) {
            String query = "SELECT amount FROM wallet WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        balance = resultSet.getDouble("amount");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return balance;
    }

    private String generatePNR() {
        String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        int pnrLength = 10;
        StringBuilder pnrBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < pnrLength; i++) {
            int randomIndex = random.nextInt(allowedCharacters.length());
            char randomChar = allowedCharacters.charAt(randomIndex);
            pnrBuilder.append(randomChar);
        }

        return pnrBuilder.toString();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel scrollingLabel;
    // End of variables declaration//GEN-END:variables
}
