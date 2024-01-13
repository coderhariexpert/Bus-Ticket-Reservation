package loginandsignup;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DisplayBus2 extends javax.swing.JFrame {
    private String username;

    String SUrl = "jdbc:mysql://localhost:3306/busresv";
    String SUser = "root";
    String SPass = "123";
    private int selectedRow;
    private Home2 home;
    private String date;

    /**
     * Creates new form DisplayBus2
     */
    public DisplayBus2(String startingpt, String destination, String date, String username, Home2 home) {
        initComponents();

        this.home = home;
        this.date = date;
        this.username = username;

        displayData(startingpt, destination, date, username);
    }

    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null}
                },
                new String[]{
                        "Title 1", "Title 2", "Title 3", "Title 4", "null", "null", "null", "null", "null"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                    false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 868, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(56, 56, 56)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15))
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

    private void displayData(String startingpt, String destination, String date, String username) {
        try (Connection connection = DriverManager.getConnection(SUrl, SUser, SPass)) {
            String query = "SELECT * FROM bus WHERE startingpt = ? AND destination = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, startingpt);
                preparedStatement.setString(2, destination);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel(
                            new Object[][]{},
                            new String[]{"ID", "AC", "Capacity", "Starting Point", "Destination", "Fare", "Departure Time", "Arrival Time", "Book Now"}
                    );
                    jTable1.setModel(model);
                    TableColumnModel columnModel = jTable1.getColumnModel();
                    columnModel.getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox()));

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
                                "Book Now"
                        };
                        model.addRow(row);
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error retrieving bus data", e);
        }
    }

    private void handleSQLException(String message, SQLException e) {
        JOptionPane.showMessageDialog(this, message + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {

            String startingpt = "StartingPoint";
            String destination = "Destination";
            String date = "2023-12-01";
            String username = "yourUsername";
            new DisplayBus2(startingpt, destination, date, username, new Home2()).setVisible(true);
        });
    }

    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }

            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                JOptionPane.showMessageDialog(button, "Book Now button clicked");
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
