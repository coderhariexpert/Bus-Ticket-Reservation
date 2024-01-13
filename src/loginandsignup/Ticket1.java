package loginandsignup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.table.DefaultTableModel;

public class Ticket1 extends javax.swing.JFrame {
    String SUrl = "jdbc:mysql://localhost:3306/busresv";
    String SUser = "root";
    String SPass = "123";

    public Ticket1(String email) {
        initComponents();
        displayData(email);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{{null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null}},
                new String[]{"Passenger_Name", "Bus_no", "Travel_Date", "Ticket_No", "Fare",
                        "Departure_Time", "Arrival_Time", "StartingPt", "Destination", "Seat No"}
        ) {
            Class[] types = new Class[]{java.lang.String.class, java.lang.Integer.class, java.util.Date.class,
                    java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class,
                    java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class};
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, false};

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 27, Short.MAX_VALUE))
        );
        pack();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ticket1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            String email = "YourEmail@example.com"; // Replace with the actual email
            new Ticket1(email).setVisible(true);
        });
    }

    private void displayData(String email) {
        try (Connection connection = DriverManager.getConnection(SUrl, SUser, SPass)) {
            String query = "SELECT * FROM booking WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                    model.setRowCount(0);
                    while (resultSet.next()) {
                        String passengerName = resultSet.getString(1);
                        int busNo = resultSet.getInt(2);
                        Date travelDate = resultSet.getDate(3);
                        int ticketNo = resultSet.getInt(4);
                        int fare = resultSet.getInt(5);
                        String departureTime = resultSet.getString(6);
                        String arrivalTime = resultSet.getString(7);
                        String startingPt = resultSet.getString(8);
                        String destination = resultSet.getString(9);
                        int seatNo = resultSet.getInt(11);
                        System.out.println("Passenger Name: " + passengerName + ", Ticket No: " + ticketNo + ", etc.");
                        model.addRow(new Object[]{
                                passengerName, busNo, travelDate, ticketNo, fare,
                                departureTime, arrivalTime, startingPt, destination, seatNo
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as needed
        } finally {
            // Close resources in a finally block
            // Close the connection, statement, and result set here if needed
        }
    }

    // Variables declaration - do not modify
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration
}
