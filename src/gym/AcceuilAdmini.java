/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gym;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Chen Zen
 */
public class AcceuilAdmini extends javax.swing.JFrame {

    /**
     * Creates new form AcceuilAdmini
     */
    public AcceuilAdmini() {
        initComponents();
        loadDashboardData(); 
        loadSubscriptionSummary();
        loadEquipementTableData();
        loadPaiementTableData();
        loadClientTableData(); 
        loadPersonnelTableData(); 
    }
     private void loadDashboardData() {
        Connection conn = null;
        PreparedStatement pstmtClients = null;
        ResultSet rsClients = null;
        PreparedStatement pstmtCoaches = null;
        ResultSet rsCoaches = null;
        PreparedStatement pstmtEquipements = null; // Corrected spelling
        ResultSet rsEquipements = null;
        PreparedStatement pstmtBenefices = null;
        ResultSet rsBenefices = null;

        String dbUrl = "jdbc:mysql://localhost:3306/gym";
        String dbUser = "root";
        String dbPassword = "votre_nouveau_mot_de_passe"; // Replace with your password

        try {
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // 1. Get Number of Clients
            String sqlClients = "SELECT COUNT(*) AS total_clients FROM client";
            pstmtClients = conn.prepareStatement(sqlClients);
            rsClients = pstmtClients.executeQuery();
            if (rsClients.next()) {
                int totalClients = rsClients.getInt("total_clients");
                jLabel2.setText(String.valueOf(totalClients)); // jLabel2 is for "Nbr" of Clients
            } else {
                jLabel2.setText("0");
            }

            // 2. Get Number of Coaches
            // Assuming your coach table is named 'coach' (as per LoginScreen)
            // And your 'coach' table has an 'Etat' column to count active/present coaches
            // If you want all coaches regardless of status, remove "WHERE Etat = 'Actif'"
            String sqlCoaches = "SELECT COUNT(*) AS total_coaches FROM coach WHERE Etat = 'Actif'"; // Or your relevant status
            // If no status column, or you want all coaches:
            // String sqlCoaches = "SELECT COUNT(*) AS total_coaches FROM coach";
            pstmtCoaches = conn.prepareStatement(sqlCoaches);
            rsCoaches = pstmtCoaches.executeQuery();
            if (rsCoaches.next()) {
                int totalCoaches = rsCoaches.getInt("total_coaches");
                jLabel5.setText(String.valueOf(totalCoaches)); // jLabel5 is for Coach count
            } else {
                jLabel5.setText("0");
            }

            // 3. Get Number of Equipments
            // Assuming your equipment table is named 'equipement' (not 'euipement' as in your SQL dump - I'll use 'equipement')
            // And it might have a status to count usable equipment
            String sqlEquipements = "SELECT COUNT(*) AS total_equipements FROM euipement "; // Or your relevant status
            // If no status column or you want all equipment:
            // String sqlEquipements = "SELECT COUNT(*) AS total_equipements FROM equipement";
            pstmtEquipements = conn.prepareStatement(sqlEquipements);
            rsEquipements = pstmtEquipements.executeQuery();
            if (rsEquipements.next()) {
                int totalEquipements = rsEquipements.getInt("total_equipements");
                jLabel12.setText(String.valueOf(totalEquipements)); // jLabel12 is for Equipment count
            } else {
                jLabel12.setText("0");
            }

            // 4. Get Total Benefices (Sum of 'montant' from 'paiment' table)
            String sqlBenefices = "SELECT SUM(montant) AS total_benefices FROM paiment";
            pstmtBenefices = conn.prepareStatement(sqlBenefices);
            rsBenefices = pstmtBenefices.executeQuery();
            if (rsBenefices.next()) {
                int totalBenefices = rsBenefices.getInt("total_benefices"); // Or getDouble if montant can have decimals
                jLabel15.setText(String.valueOf(totalBenefices) + " DA"); // jLabel15 is for Benefices
            } else {
                jLabel15.setText("0 DA");
            }

        } catch (SQLException e) {
            // Handle exceptions: Log them and set labels to an error state or "N/A"
            Logger.getLogger(AcceuilAdmini.class.getName()).log(Level.SEVERE, "Error loading dashboard data", e);
            jLabel2.setText("Erreur");
            jLabel5.setText("Erreur");
            jLabel12.setText("Erreur");
            jLabel15.setText("Erreur");
            // Optionally, show a JOptionPane, but for a dashboard, often better to fail gracefully
            // JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close all resources in individual try-catch blocks
            try { if (rsClients != null) rsClients.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmtClients != null) pstmtClients.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (rsCoaches != null) rsCoaches.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmtCoaches != null) pstmtCoaches.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (rsEquipements != null) rsEquipements.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmtEquipements != null) pstmtEquipements.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (rsBenefices != null) rsBenefices.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmtBenefices != null) pstmtBenefices.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
     private void loadSubscriptionSummary() {
    // The table in your UI for Abonnements is jTable2
    // Its columns are: "Nom", "Prix", "Type", "etat", "Nombre de clients"
    // We need to display: "Type", "Prix", "Nombre de clients"
    // We can adjust the model or just fill the relevant columns.
    // For simplicity, let's create a model that matches what we want to display.

    String[] columnNames = {"Type", "Prix (DA)", "Nombre de Clients Actifs"};
    DefaultTableModel model = new DefaultTableModel(null, columnNames); // Create a new model
    jTable2.setModel(model); // Set the new model to jTable2

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String dbUrl = "jdbc:mysql://localhost:3306/gym";
    String dbUser = "root";
    String dbPassword = "votre_nouveau_mot_de_passe"; // Replace!

    try {
        conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        String sql = "SELECT a.type, a.Prix, COUNT(DISTINCT a.client_id) AS nombre_de_clients " +
                     "FROM abonnement a " +
                     "WHERE a.Status = 'actif' AND a.`Date du fin` >= CURDATE() " +
                     "GROUP BY a.type, a.Prix " +
                     "ORDER BY a.type";
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();

        while (rs.next()) {
            String type = rs.getString("type");
            int prix = rs.getInt("Prix");
            int nombreClients = rs.getInt("nombre_de_clients");

            model.addRow(new Object[]{
                type,
                prix,
                nombreClients
            });
        }

        if (model.getRowCount() == 0) {
            // Optionally, display a message if no active subscriptions found
            // model.addRow(new Object[]{"Aucun abonnement actif trouvé", null, null});
        }

    } catch (SQLException e) {
        Logger.getLogger(AcceuilAdmini.class.getName()).log(Level.SEVERE, "Error loading subscription summary", e);
        JOptionPane.showMessageDialog(this, "Erreur de chargement du résumé des abonnements: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
        // Clear the table in case of error
        model.setRowCount(0);
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
     private void loadEquipementTableData() {
    // The table in your UI for Equipements is jTable4
    // Its columns are: "Nom", "Type", "Marque", "Status"

    // We can use the existing model if the columns match exactly what the DB provides
    // or create a new one for more control. Let's use the existing one for now
    // and assume the column order in the DB query will match the table.
    DefaultTableModel model = (DefaultTableModel) jTable4.getModel();
    model.setRowCount(0); // Clear existing rows before loading new ones

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String dbUrl = "jdbc:mysql://localhost:3306/gym";
    String dbUser = "root";
    String dbPassword = "votre_nouveau_mot_de_passe"; // Replace!

    try {
        conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        // Assuming your table is named 'equipement' and has these columns
        // The 'id' column from the DB is not displayed in this table as per your UI.
        String sql = "SELECT Nom, Type, Marque, Status FROM euipement ORDER BY Nom";
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();

        while (rs.next()) {
            String nom = rs.getString("Nom");
            String type = rs.getString("Type");
            String marque = rs.getString("Marque");
            String status = rs.getString("Status");

            model.addRow(new Object[]{
                nom,
                type,
                marque,
                status
            });
        }

        if (model.getRowCount() == 0) {
            // Optionally, display a message if no equipment is found
            // model.addRow(new Object[]{"Aucun équipement trouvé", null, null, null});
        }

    } catch (SQLException e) {
        Logger.getLogger(AcceuilAdmini.class.getName()).log(Level.SEVERE, "Error loading equipement data", e);
        JOptionPane.showMessageDialog(this, "Erreur de chargement des équipements: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
        model.setRowCount(0); // Clear table on error
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
     
private void loadPaiementTableData() {
    // jTable5 columns from screenshot: "Id Transaction", "Client", "Montant", "date", "Client" (assuming last is "heure")
    // Desired columns for better readability: "ID Transaction", "Nom Client", "Prénom Client", "Montant", "Date", "Heure"
    
    String[] columnNames = {"ID Transaction", "Nom Client", "Prénom Client", "Montant (DA)", "Date", "Heure"};
    DefaultTableModel model = new DefaultTableModel(null, columnNames);
    jTable5.setModel(model); // Set the new model to jTable5

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String dbUrl = "jdbc:mysql://localhost:3306/gym";
    String dbUser = "root";
    String dbPassword = "votre_nouveau_mot_de_passe"; // Replace!

    // Formatters for displaying date and time nicely in the table
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    try {
        conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        // SQL query to get payments and join with client table to get client names
        String sql = "SELECT p.id, c.Nom AS client_nom, c.Prenom AS client_prenom, p.montant, p.date, p.heure " +
                     "FROM paiment p " +
                     "JOIN client c ON p.Client = c.id " + // Assuming p.Client stores client.id
                     "ORDER BY p.date DESC, p.heure DESC"; // Show most recent payments first
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();

        while (rs.next()) {
            int transactionId = rs.getInt("id");
            String clientNom = rs.getString("client_nom");
            String clientPrenom = rs.getString("client_prenom");
            int montant = rs.getInt("montant");
            java.sql.Date dateSql = rs.getDate("date");
            java.sql.Time timeSql = rs.getTime("heure");

            String formattedDate = (dateSql != null) ? dateSql.toLocalDate().format(dateFormatter) : "N/A";
            String formattedTime = (timeSql != null) ? timeSql.toLocalTime().format(timeFormatter) : "N/A";

            model.addRow(new Object[]{
                transactionId,
                clientNom,
                clientPrenom,
                montant,
                formattedDate,
                formattedTime
            });
        }

        if (model.getRowCount() == 0) {
            // Optionally, display a message if no payments found
            // model.addRow(new Object[]{"Aucun paiement trouvé", null, null, null, null, null});
        }

    } catch (SQLException e) {
        Logger.getLogger(AcceuilAdmini.class.getName()).log(Level.SEVERE, "Error loading paiement data", e);
        JOptionPane.showMessageDialog(this, "Erreur de chargement des paiements: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
        model.setRowCount(0); // Clear table on error
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
// Add a new DefaultTableModel for the personnel/coach table
private DefaultTableModel personnelTableModel; 

// Method to load personnel (coach) data into jTable6
private void loadPersonnelTableData() {
    // Columns for jTable6: "id coach", "Nom", "Prenom", "Adresse", "N° Tel", "Etat"
    String[] columnNames = {"id coach", "Nom", "Prenom", "Adresse", "N° Tel", "Etat"};
    
    if (personnelTableModel == null) {
        personnelTableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // "id coach" (column 0) is not editable
                return column != 0; 
            }
        };
        jTable6.setModel(personnelTableModel);
        // Add a TableModelListener to detect changes
        addPersonnelTableListener();
    } else {
        personnelTableModel.setRowCount(0); // Clear existing rows
    }

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String dbUrl = "jdbc:mysql://localhost:3306/gym";
    String dbUser = "root";
    String dbPassword = "votre_nouveau_mot_de_passe"; // Replace!

    try {
        conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        // Assuming your coach table is named 'coach'
        String sql = "SELECT id, Nom, Prenom, Adresse, `N tel`, Etat FROM coach ORDER BY id";
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();

        while (rs.next()) {
            personnelTableModel.addRow(new Object[]{
                rs.getInt("id"),
                rs.getString("Nom"),
                rs.getString("Prenom"),
                rs.getString("Adresse"),
                rs.getString("N tel"),
                rs.getString("Etat")
            });
        }
    } catch (SQLException e) {
        Logger.getLogger(AcceuilAdmini.class.getName()).log(Level.SEVERE, "Error loading personnel data", e);
        JOptionPane.showMessageDialog(this, "Erreur de chargement du personnel: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}

// Add a TableModelListener to jTable6
private void addPersonnelTableListener() {
    personnelTableModel.addTableModelListener(new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (row >= 0 && column >= 0) {
                    int coachId = (Integer) personnelTableModel.getValueAt(row, 0);
                    Object newValue = personnelTableModel.getValueAt(row, column);
                    String columnNameInTable = personnelTableModel.getColumnName(column);
                    
                    String dbColumnName = "";
                    switch (columnNameInTable) {
                        case "Nom": dbColumnName = "Nom"; break;
                        case "Prenom": dbColumnName = "Prenom"; break;
                        case "Adresse": dbColumnName = "Adresse"; break;
                        case "N° Tel": dbColumnName = "`N tel`"; break;
                        case "Etat": dbColumnName = "Etat"; break;
                        default:
                            System.out.println("Column " + columnNameInTable + " is not updatable for personnel or not recognized.");
                            return; 
                    }

                    int confirm = JOptionPane.showConfirmDialog(
                        AcceuilAdmini.this,
                        "Voulez-vous enregistrer la modification pour le coach ID " + coachId + " ?\n" +
                        "Champ: " + columnNameInTable + "\nNouvelle valeur: " + newValue,
                        "Confirmation de mise à jour Personnel",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        updateCoachInDatabase(coachId, dbColumnName, newValue);
                    } else {
                        // User canceled, consider reverting the change or reloading
                        System.out.println("Personnel update canceled by admin.");
                        // loadPersonnelTableData(); // To revert visual changes
                    }
                }
            }
        }
    });
}

// Method to update the coach in the database
private void updateCoachInDatabase(int coachId, String dbColumnName, Object newValue) {
    Connection conn = null;
    PreparedStatement pstmt = null;

    String dbUrl = "jdbc:mysql://localhost:3306/gym";
    String dbUser = "root";
    String dbPassword = "votre_nouveau_mot_de_passe";

    String sql = "UPDATE coach SET " + dbColumnName + " = ? WHERE id = ?";

    try {
        conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        pstmt = conn.prepareStatement(sql);

        if (newValue instanceof String) {
            pstmt.setString(1, (String) newValue);
        } else if (newValue instanceof Integer) { // Should not happen for coach fields other than ID
            pstmt.setInt(1, (Integer) newValue);
        } 
        // Add other type checks if necessary, though coach fields are mostly VARCHAR
        else {
            pstmt.setString(1, newValue.toString()); 
        }
        
        pstmt.setInt(2, coachId);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Coach ID " + coachId + " mis à jour avec succès !", "Mise à Jour Réussie", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Échec de la mise à jour du coach ID " + coachId + ". Coach non trouvé ou valeur inchangée.", "Erreur de Mise à Jour", JOptionPane.ERROR_MESSAGE);
            // loadPersonnelTableData(); // To revert
        }

    } catch (SQLException e) {
        Logger.getLogger(AcceuilAdmini.class.getName()).log(Level.SEVERE, "Error updating coach data for ID " + coachId, e);
        JOptionPane.showMessageDialog(this, "Erreur de base de données lors de la mise à jour du coach: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
        // loadPersonnelTableData(); // To revert
    } finally {
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}


private DefaultTableModel clientTableModel; // Make table model a class member for easier access

private void loadClientTableData() {
    // Columns for jTable1: "Id Client", "Nom", "Prenom", "Adresse", "N° Tel", "Email", "Sexe", "Solde"
    // (Omitting "Status" as it's not in client table schema, and "Mot de passe" for security)
    String[] columnNames = {"Id Client", "Nom", "Prenom", "Adresse", "N° Tel", "Email", "Sexe", "Solde (DA)"};
    
    // Initialize the table model if it hasn't been, or clear it
    if (clientTableModel == null) {
        clientTableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all columns editable EXCEPT the "Id Client" column (column 0)
                return column != 0; 
            }
        };
        jTable1.setModel(clientTableModel);
        // Add a TableModelListener to detect changes
        addClientTableListener();
    } else {
        clientTableModel.setRowCount(0); // Clear existing rows
    }

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String dbUrl = "jdbc:mysql://localhost:3306/gym";
    String dbUser = "root";
    String dbPassword = "votre_nouveau_mot_de_passe"; // Replace!

    try {
        conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        String sql = "SELECT id, Nom, Prenom, Adresse, `N tel`, Email, sexe, solde FROM client ORDER BY id";
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();

        while (rs.next()) {
            clientTableModel.addRow(new Object[]{
                rs.getInt("id"),
                rs.getString("Nom"),
                rs.getString("Prenom"),
                rs.getString("Adresse"),
                rs.getString("N tel"),
                rs.getString("Email"),
                rs.getString("sexe"),
                rs.getInt("solde") // Or rs.getBigDecimal("solde") if it were decimal
            });
        }
    } catch (SQLException e) {
        Logger.getLogger(AcceuilAdmini.class.getName()).log(Level.SEVERE, "Error loading client data", e);
        JOptionPane.showMessageDialog(this, "Erreur de chargement des clients: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
    } finally {
        // Close resources
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}

// 2. Add a TableModelListener to jTable1
private void addClientTableListener() {
    clientTableModel.addTableModelListener(new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                // Ensure the event is for a valid cell and not header
                if (row >= 0 && column >= 0) {
                    // Get the ID of the client whose data was changed (from the non-editable first column)
                    int clientId = (Integer) clientTableModel.getValueAt(row, 0);
                    // Get the new value from the edited cell
                    Object newValue = clientTableModel.getValueAt(row, column);
                    // Get the column name that was edited
                    String columnNameInTable = clientTableModel.getColumnName(column);
                    
                    // Map table column name to database column name
                    String dbColumnName = "";
                    switch (columnNameInTable) {
                        case "Nom": dbColumnName = "Nom"; break;
                        case "Prenom": dbColumnName = "Prenom"; break;
                        case "Adresse": dbColumnName = "Adresse"; break;
                        case "N° Tel": dbColumnName = "`N tel`"; break; // Use backticks for special chars
                        case "Email": dbColumnName = "Email"; break;
                        case "Sexe": dbColumnName = "sexe"; break;
                        case "Solde (DA)": dbColumnName = "solde"; break;
                        default:
                            // If column is not one we want to update (e.g., ID or unknown)
                            System.out.println("Column " + columnNameInTable + " is not updatable directly or not recognized.");
                            return; 
                    }

                    // Ask for confirmation before updating the database
                    int confirm = JOptionPane.showConfirmDialog(
                        AcceuilAdmini.this, // Parent component
                        "Voulez-vous enregistrer la modification pour le client ID " + clientId + " ?\n" +
                        "Champ: " + columnNameInTable + "\nNouvelle valeur: " + newValue,
                        "Confirmation de mise à jour",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        updateClientInDatabase(clientId, dbColumnName, newValue, row, column);
                    } else {
                        // User canceled, revert the change in the table (or reload data)
                        // For simplicity now, we'll just log it. A more robust solution would revert.
                        System.out.println("Update canceled by admin. Consider reverting table cell value or reloading.");
                        // To revert, you'd need to store the old value before edit or reload the row/table.
                        // loadClientTableData(); // Simplest way to revert all, but might be disruptive
                    }
                }
            }
        }
    });
}

// 3. Method to update the client in the database
private void updateClientInDatabase(int clientId, String dbColumnName, Object newValue, int viewRow, int viewColumn) {
    Connection conn = null;
    PreparedStatement pstmt = null;

    String dbUrl = "jdbc:mysql://localhost:3306/gym";
    String dbUser = "root";
    String dbPassword = "votre_nouveau_mot_de_passe";

    // Construct the SQL UPDATE statement dynamically (be careful with SQL injection if column names came from user input)
    // Here, dbColumnName comes from our predefined switch-case, so it's safer.
    String sql = "UPDATE client SET " + dbColumnName + " = ? WHERE id = ?";

    try {
        conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        pstmt = conn.prepareStatement(sql);

        // Set the new value based on its type
        if (newValue instanceof String) {
            pstmt.setString(1, (String) newValue);
        } else if (newValue instanceof Integer) {
            pstmt.setInt(1, (Integer) newValue);
        } else if (newValue instanceof Double) { // If solde could be double
            pstmt.setDouble(1, (Double) newValue);
        } else if (newValue instanceof java.sql.Date) { // If you had a date column to edit
             pstmt.setDate(1, (java.sql.Date) newValue);
        }
        // Add more types if needed (e.g., BigDecimal for solde if it were decimal)
        else {
            // Fallback to String if type is unknown, might cause issues
            pstmt.setString(1, newValue.toString()); 
        }
        
        pstmt.setInt(2, clientId);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Client ID " + clientId + " mis à jour avec succès !", "Mise à Jour Réussie", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Échec de la mise à jour du client ID " + clientId + ". Client non trouvé ou valeur inchangée.", "Erreur de Mise à Jour", JOptionPane.ERROR_MESSAGE);
            // Reload data to revert visual change if DB update failed
            // loadClientTableData(); // Consider this
        }

    } catch (SQLException e) {
        Logger.getLogger(AcceuilAdmini.class.getName()).log(Level.SEVERE, "Error updating client data for ID " + clientId, e);
        JOptionPane.showMessageDialog(this, "Erreur de base de données lors de la mise à jour: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
        // Reload data to revert visual change on DB error
        // loadClientTableData(); // Consider this
    } finally {
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}

// 4. Call `loadClientTableData()` when the "Clients" tab is selected.
// Modify the ChangeListener in your AcceuilAdmini constructor:
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        tabbedPaneCustom2 = new raven.tabbed.TabbedPaneCustom();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/client.png"))); // NOI18N
        jLabel9.setToolTipText("");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel1.setText("Clients :");

        jLabel2.setText("Nbr");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/des-exercices-detirement.png"))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel4.setText("Coachs :");

        jLabel5.setText("jLabel5");

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/exercice.png"))); // NOI18N

        jLabel11.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel11.setText("Equipements : ");

        jLabel12.setText("jLabel12");

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/main-avec-money-gear (1).png"))); // NOI18N

        jLabel13.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel13.setText("Benifices :");

        jLabel15.setText("jLabel15");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(99, 99, 99)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(22, 22, 22)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addGap(85, 85, 85))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(jLabel3)
                        .addGap(73, 73, 73)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)))
                .addGap(45, 45, 45)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel15))
                    .addComponent(jLabel14))
                .addContainerGap(281, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel3)
                    .addComponent(jLabel10)
                    .addComponent(jLabel14))
                .addGap(88, 88, 88)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel4))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(jLabel12)
                        .addComponent(jLabel13)
                        .addComponent(jLabel15)))
                .addContainerGap(259, Short.MAX_VALUE))
        );

        tabbedPaneCustom2.addTab("Acceuil", jPanel2);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id Client", "Nom", "Prenom", "Adresse", "N° Tel", "Email", "Status", "Sexe", "Solde"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Byte.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 835, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(237, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
        );

        tabbedPaneCustom2.addTab("Clients", jPanel3);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Type", "Prix", "Nombre de clients"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 712, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(327, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(130, Short.MAX_VALUE))
        );

        tabbedPaneCustom2.addTab("Abonnements", jPanel4);

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nom", "Type", "Marque", "Status"
            }
        ));
        jScrollPane4.setViewportView(jTable4);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 874, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(207, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(107, Short.MAX_VALUE))
        );

        tabbedPaneCustom2.addTab("Equipements", jPanel6);

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id Transaction", "Client", "Montant", "date", "heure"
            }
        ));
        jScrollPane5.setViewportView(jTable5);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel7.setText("Benifice Total");

        jLabel8.setText("jLabel8");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 758, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(365, 365, 365)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)))
                .addContainerGap(271, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(40, 40, 40))
        );

        tabbedPaneCustom2.addTab("Paiements", jPanel7);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1087, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 603, Short.MAX_VALUE)
        );

        tabbedPaneCustom2.addTab("Messagerie", jPanel8);

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "id coach", "Nom", "Prenom", "Adresse", "N° Tel", "Etat"
            }
        ));
        jScrollPane6.setViewportView(jTable6);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 735, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(318, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(131, Short.MAX_VALUE))
        );

        tabbedPaneCustom2.addTab("Personnel", jPanel9);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel6.setText("Voullez vous vraiment se deconnecter ?");

        jButton1.setText("Se Deconnecter");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(509, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(234, 234, 234))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(370, 370, 370))))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap(494, Short.MAX_VALUE))
        );

        tabbedPaneCustom2.addTab("Deconnexion", jPanel10);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPaneCustom2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPaneCustom2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
         JOptionPane.showMessageDialog(this, "Déconnexion réussie.");
                dispose();
                new LoginScreen("Administrateur").setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AcceuilAdmini.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AcceuilAdmini.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AcceuilAdmini.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AcceuilAdmini.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AcceuilAdmini().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private raven.tabbed.TabbedPaneCustom tabbedPaneCustom2;
    // End of variables declaration//GEN-END:variables
}
