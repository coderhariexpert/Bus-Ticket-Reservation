package loginandsignup;

import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author 
 */
public class Ticket3 extends javax.swing.JFrame {
    private Home2 home;
    private String username;
    String SUrl = "jdbc:mysql://localhost:3306/busresv";
    String SUser = "root";
    String SPass = "123";

    /**
     * Creates new form Ticket3
     */
    public Ticket3(String email, Home2 home) {
        initComponents();
        this.username = email;
        this.home = home;
        displayData(email);
        goBackToHome();
        addButtonToTable();
    }

    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null, null}
                },
                new String[]{
                        "Ticket Number", "Total Passengers", "Bus Number", "From", "To", "Departure_Time", "Arrival_Time", "Travel Date", "Fare", "View Ticket"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                    false, false, false, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jTable1.setToolTipText("");
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable1.setRowHeight(50);
        jTable1.setUpdateSelectionOnSort(false);
        jScrollPane1.setViewportView(jTable1);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/loginandsignup/THE BLUE BUS1.png"))); // NOI18N
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
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(55, 55, 55)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(85, 85, 85))
        );

        pack();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        goBackToHome();
    }

    private void addButtonToTable() {
        int buttonColumnIndex = 9; // Assuming the last column is the button column
        jTable1.getColumnModel().getColumn(buttonColumnIndex).setCellRenderer(new ButtonRenderer());
        jTable1.getColumnModel().getColumn(buttonColumnIndex).setCellEditor(new ButtonEditor(new JCheckBox()));
    }
    

    private void displayData(String email) {
        try (Connection connection = DriverManager.getConnection(SUrl, SUser, SPass)) {
            String query = "SELECT ticket_no, COUNT(*) as total_passengers, " +
                    "MAX(bus_no) as bus_no, MAX(travel_date) as travel_date, " +
                    "SUM(fare) as total_fare, SUM(fare * 0.18) as gst_amount, " +
                    "MAX(departure) as departure, MAX(arrival) as arrival, " +
                    "MAX(startingpt) as startingpt, MAX(destination) as destination " +
                    "FROM booking WHERE email = ? AND passenger_name IS NOT NULL GROUP BY ticket_no";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel(
                            new Object[][]{},
                            new String[]{
                                    "Ticket Number", "Total Passengers", "Bus Number", "From", "To", "Departure_Time", "Arrival_Time", "Travel Date", "Fare", "View Ticket"
                            }
                    ) {
                        boolean[] canEdit = new boolean[]{
                                false, false, false, false, false, false, false, false, false, true
                        };

                        public boolean isCellEditable(int rowIndex, int columnIndex) {
                            return canEdit[columnIndex];
                        }
                    };

                    while (resultSet.next()) {
                        int ticketNo = resultSet.getInt("ticket_no");
                        int totalPassengers = resultSet.getInt("total_passengers");
                        int busNo = resultSet.getInt("bus_no");
                        Date travelDate = resultSet.getDate("travel_date");
                        double totalFare = resultSet.getDouble("total_fare");
                        double gstAmount = resultSet.getDouble("gst_amount");
                        double totalFareWithGST = totalFare + gstAmount;
                        String departureTime = resultSet.getString("departure");
                        String arrivalTime = resultSet.getString("arrival");
                        String startingPt = resultSet.getString("startingpt");
                        String destination = resultSet.getString("destination");

                        model.addRow(new Object[]{
                                ticketNo, totalPassengers, busNo, startingPt, destination,
                                departureTime, arrivalTime, travelDate, totalFareWithGST, "View Ticket"
                        });
                    }

                    // Set the model to the table
                    jTable1.setModel(model);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }

    private void goBackToHome() {
        home.setUser(username);
        home.setVisible(true);
        home.pack();
        home.setLocationRelativeTo(null);
        this.dispose();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            String email = "YourEmail@example.com";
            Home2 home = new Home2(); // Replace with your actual instantiation logic for Home2
            new Ticket3(email, home).setVisible(true);
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration

    private static class ButtonRenderer extends DefaultTableCellRenderer {
        private final JButton button;

        public ButtonRenderer() {
            this.button = new JButton("View Ticket");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return button;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("View Ticket");
            button.setOpaque(true);
            button.addActionListener(e -> {
                int row = jTable1.getSelectedRow();
                if (row != -1) {
                    // Retrieve the ticket number from the selected row
                    int ticketNo = (int) jTable1.getValueAt(row, 0);
                    // Open the "View Ticket" frame with the ticket number
                    TicketFrame viewTicketFrame = new TicketFrame(ticketNo,username,home);
                    viewTicketFrame.setVisible(true);
                    dispose();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }

            button.setText((value == null) ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }
}
