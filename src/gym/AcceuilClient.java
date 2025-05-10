/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gym;

import javax.swing.JOptionPane;
import java.sql.*;
import java.sql.Date; // For java.sql.Date
import java.time.LocalDate; // For date calculations
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; // For formatting dates for display
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Chen Zen
 */
public class AcceuilClient extends javax.swing.JFrame {

    /**
     * Creates new form AcceuilClient
     */
    
     private String clientEmail; // To store the logged-in client's email
      private int clientId; // To store the logged-in client's ID
       private int clientSolde; // Changed to int
      
      // To store details of the currently displayed active subscription for easier renewal
    private static class ActiveSubscriptionDetails {
        String type;
        int prix;
        int originalDurationDays; // e.g., 7 for hebdomadaire, 30 for mensuel, 365 for annuel

        ActiveSubscriptionDetails(String type, int prix, int originalDurationDays) {
            this.type = type;
            this.prix = prix;
            this.originalDurationDays = originalDurationDays;
        }
    }
    private ActiveSubscriptionDetails currentActiveSubDetails = null;

     
        public AcceuilClient(String clientEmail) throws SQLException {
        this.clientEmail = clientEmail;
        initComponents();
        setTitle("Accueil Client - " + clientEmail); // Set a more informative title
        setLocationRelativeTo(null); // Center the window
        try {
            loadClientDataAndSubscription(); 
            loadPaymentHistory(); // Call it here
        } catch (SQLException ex) { /* ... error handling ... */ }
         
    }
        
    public AcceuilClient() {
        initComponents();
        System.out.println("AcceuilClient default constructor called - clientEmail will be null. Data loading will be skipped.");
        // Initially disable renew button as no subscription is loaded
        jButton3.setEnabled(false); 
        clearAbonnementActuelLabels(); // Clear labels if default constructor is used
        this.clientSolde = 0; // Initialize solde for default constructor
    }
    
     private void loadClientDataAndSubscription() throws SQLException {
        if (this.clientEmail == null || this.clientEmail.isEmpty()) {
            System.err.println("Client email not provided. Cannot load data.");
            // Clear profile fields
            jTextField1.setText("");
            jTextField2.setText("");
            jTextField3.setText("");
            jTextField4.setText("");
            jTextField5.setText("");
            jTextField6.setText("");
            jComboBox3.setSelectedIndex(0);
            // Clear subscription fields and disable renew
            clearAbonnementActuelLabels();
            jButton3.setEnabled(false);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmtClient = null;
        ResultSet rsClient = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "votre_nouveau_mot_de_passe");
            
            // 1. Load Client Data and get client_id
            String sqlClient = "SELECT id, Nom, Prenom, Adresse, `date de naissance`, Email, `Mot de passe`, sexe , solde FROM client WHERE Email = ?";
            pstmtClient = conn.prepareStatement(sqlClient);
            pstmtClient.setString(1, this.clientEmail);
            rsClient = pstmtClient.executeQuery();

            if (rsClient.next()) {
                this.clientId = rsClient.getInt("id"); // Store client ID
                this.clientSolde = rsClient.getInt("solde"); // Load solde as int
                // rsClient.getInt() returns 0 if the SQL value is NULL, which is acceptable for solde.
                jTextField1.setText(rsClient.getString("Nom"));
                jTextField2.setText(rsClient.getString("Prenom"));
                jTextField3.setText(rsClient.getString("Adresse"));
                jTextField4.setText(rsClient.getString("date de naissance")); // Column name from your schema
                jTextField5.setText(rsClient.getString("Email"));
                jTextField6.setText(rsClient.getString("Mot de passe"));
                
                String sexe = rsClient.getString("sexe"); // Column name from your schema
                if ("Male".equalsIgnoreCase(sexe) || "Homme".equalsIgnoreCase(sexe)) {
                    jComboBox3.setSelectedItem("Male");
                } else if ("Female".equalsIgnoreCase(sexe) || "Femme".equalsIgnoreCase(sexe)) {
                    jComboBox3.setSelectedItem("Female");
                } else {
                    jComboBox3.setSelectedIndex(0); 
                }
                
                // 2. Now load subscription data using this.clientId
                loadAbonnementData(conn);
                  jLabel41.setText(String.valueOf(this.clientSolde) + " DA");

            } else {
                JOptionPane.showMessageDialog(this, "Client non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                clearAbonnementActuelLabels();
                jButton3.setEnabled(false);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur DB (Client): " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            clearAbonnementActuelLabels();
            jButton3.setEnabled(false);
        } finally {
            
            // The connection will be closed by loadAbonnementData if it was passed and used
        }
    }
      private void clearAbonnementActuelLabels() {
        jLabel15.setText(""); // Type
        jLabel17.setText(""); // Date du debut
        jLabel19.setText(""); // Date du fin
        jLabel36.setText(""); // Prix
        jLabel21.setText(""); // Status
    }

    private void loadAbonnementData(Connection connPassed) {
        // This method assumes clientId is already set.
        if (this.clientId == 0) {
            System.err.println("Client ID not set. Cannot load subscription data.");
            clearAbonnementActuelLabels();
            jButton3.setEnabled(false);
            return;
        }

        Connection conn = connPassed;
        boolean newConnectionOpened = false;
        PreparedStatement pstmtSub = null;
        ResultSet rsSub = null;
        currentActiveSubDetails = null; // Reset
        DateTimeFormatter dbDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE; // Dates in DB are YYYY-MM-DD
        DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "votre_nouveau_mot_de_passe");
                newConnectionOpened = true;
            }

            String sqlSub = "SELECT type, `Date de debut`, `Date du fin`, Prix, Status FROM abonnement " +
                            "WHERE client_id = ? AND Status = 'actif' " +
                            "ORDER BY `Date du fin` DESC, `Date de debut` DESC LIMIT 1";
            pstmtSub = conn.prepareStatement(sqlSub);
            pstmtSub.setInt(1, this.clientId);
            rsSub = pstmtSub.executeQuery();

            if (rsSub.next()) {
                String type = rsSub.getString("type");
                Date dateDebutSQL = rsSub.getDate("Date de debut");
                Date dateFinSQL = rsSub.getDate("Date du fin");
                int prix = rsSub.getInt("Prix");
                String status = rsSub.getString("Status");

                jLabel15.setText(type);
                jLabel17.setText(dateDebutSQL != null ? dateDebutSQL.toLocalDate().format(displayDateFormatter) : "");
                jLabel19.setText(dateFinSQL != null ? dateFinSQL.toLocalDate().format(displayDateFormatter) : "");
                jLabel36.setText(String.valueOf(prix) + " DA");
                jLabel21.setText(status);
                jButton3.setEnabled(true); // Enable renew button

                int duration = 0;
                if ("hebdomadaire".equalsIgnoreCase(type)) duration = 7;
                else if ("mensuel".equalsIgnoreCase(type)) duration = 30;
                else if ("annuel".equalsIgnoreCase(type)) duration = 365;
                currentActiveSubDetails = new ActiveSubscriptionDetails(type, prix, duration);

            } else {
                clearAbonnementActuelLabels();
                jLabel15.setText("Aucun abonnement actif");
                jButton3.setEnabled(false); // Disable renew if no active subscription
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur DB (Abonnement): " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            clearAbonnementActuelLabels();
            jButton3.setEnabled(false);
        } finally {
            try { if (rsSub != null) rsSub.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmtSub != null) pstmtSub.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (newConnectionOpened && conn != null) { // Only close if this method opened it
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            } else if (connPassed != null && conn == null) { // If connPassed was used but became null somehow (e.g. closed externally)
                // This case is less likely if connPassed is managed correctly by caller
            }
            // If connPassed was provided and used, the caller (loadClientDataAndSubscription) should close it if it opened it.
            // For simplicity now, if loadClientDataAndSubscription opens it, it closes it. If loadAbonnementData needs one, it opens/closes its own.
            // Let's refine: loadClientDataAndSubscription will manage its connection. loadAbonnementData will take the open conn.
        }
    }
    
    // Call this method from loadClientData after getting client ID and its own DB operations are done.
    private void loadAbonnementData() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "votre_nouveau_mot_de_passe");
            loadAbonnementData(conn); // Call the version that takes a connection
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "Erreur de connexion pour charger abonnement: " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

private void inscrireAbonnement(String typeAbonnement, int prixAbonnement, int dureeEnJours) {
    if (this.clientId == 0) {
        JOptionPane.showMessageDialog(this, "Erreur: ID Client non disponible.", "Erreur", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (this.clientSolde < prixAbonnement) {
        JOptionPane.showMessageDialog(this,
                "Solde insuffisant (" + this.clientSolde + " DA). " +
                "Le prix de l'abonnement est de " + prixAbonnement + " DA.",
                "Solde Insuffisant", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int newSoldeCalculated = this.clientSolde - prixAbonnement;
    // The confirmation dialog for subscription is already handled by the calling button action (jButton3, jButton4, etc.)
    // So, we can proceed directly if this method is called.
    // However, for direct calls from jButton4, jButton5, jButton6, the confirmation is still good.
    // For consistency, let's keep a confirmation here or ensure it's always done before calling.
    // The previous version of jButton3 did the confirm THEN called this. That's fine.
    // Let's assume confirmation for the specific action (new sub or renewal) has happened.

    Connection conn = null;
    PreparedStatement pstmtUpdateOldSub = null;
    PreparedStatement pstmtInsertNewSub = null;
    PreparedStatement pstmtUpdateSolde = null;
    PreparedStatement pstmtInsertPayment = null; // For the paiment table

    try {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "votre_nouveau_mot_de_passe");
        conn.setAutoCommit(false); // Start transaction

        // 1. Deactivate any existing active subscription for this client
        String sqlUpdateOld = "UPDATE abonnement SET Status = 'pas actif' WHERE client_id = ? AND Status = 'actif'";
        pstmtUpdateOldSub = conn.prepareStatement(sqlUpdateOld);
        pstmtUpdateOldSub.setInt(1, this.clientId);
        pstmtUpdateOldSub.executeUpdate();

        // 2. Insert the new subscription
        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = dateDebut.plusDays(dureeEnJours);
        String sqlInsertNew = "INSERT INTO abonnement (client_id, type, `Date de debut`, `Date du fin`, Prix, Status) " +
                              "VALUES (?, ?, ?, ?, ?, 'actif')";
        pstmtInsertNewSub = conn.prepareStatement(sqlInsertNew);
        pstmtInsertNewSub.setInt(1, this.clientId);
        pstmtInsertNewSub.setString(2, typeAbonnement);
        pstmtInsertNewSub.setDate(3, java.sql.Date.valueOf(dateDebut)); // Use java.sql.Date
        pstmtInsertNewSub.setDate(4, java.sql.Date.valueOf(dateFin));   // Use java.sql.Date
        pstmtInsertNewSub.setInt(5, prixAbonnement);
        pstmtInsertNewSub.executeUpdate();

        // 3. Update client's solde
        String sqlUpdateClientSolde = "UPDATE client SET solde = ? WHERE id = ?";
        pstmtUpdateSolde = conn.prepareStatement(sqlUpdateClientSolde);
        pstmtUpdateSolde.setInt(1, newSoldeCalculated);
        pstmtUpdateSolde.setInt(2, this.clientId);
        pstmtUpdateSolde.executeUpdate();

        // 4. Insert into paiment table
       
       // Get current date and time
       java.sql.Timestamp paymentDateTime = new java.sql.Timestamp(System.currentTimeMillis());


        String sqlInsertPaiment = "INSERT INTO paiment (Client, montant, date , heure) VALUES (?, ?, ? , ?)";
        pstmtInsertPayment = conn.prepareStatement(sqlInsertPaiment);
        pstmtInsertPayment.setInt(1, this.clientId);
        pstmtInsertPayment.setInt(2, prixAbonnement); // The amount paid for the subscription
        
// Option 1: Store date and time in separate fields
java.sql.Date paymentDate = new java.sql.Date(paymentDateTime.getTime());
java.sql.Time paymentTime = new java.sql.Time(paymentDateTime.getTime());
pstmtInsertPayment.setDate(3, paymentDate);
pstmtInsertPayment.setTime(4, paymentTime);
        pstmtInsertPayment.executeUpdate();

        conn.commit(); // Commit transaction (includes all 4 operations now)

        JOptionPane.showMessageDialog(this, "Abonnement " + typeAbonnement + " enregistré et paiement enregistré avec succès!\nNouveau solde: " + newSoldeCalculated + " DA.", "Succès", JOptionPane.INFORMATION_MESSAGE);

        this.clientSolde = newSoldeCalculated; // Update local clientSolde
        jLabel41.setText(String.valueOf(this.clientSolde) + " DA"); // Update solde display

        // Refresh all relevant data after successful operation
        loadClientDataAndSubscription(); // This will also call loadAbonnementData

    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback(); // Rollback on error
                JOptionPane.showMessageDialog(this, "Transaction annulée en raison d'une erreur.", "Erreur de Transaction", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                Logger.getLogger(AcceuilClient.class.getName()).log(Level.SEVERE, "Erreur lors du rollback de la transaction", ex);
            }
        }
        JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription à l'abonnement/paiement: " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(AcceuilClient.class.getName()).log(Level.SEVERE, "Error in inscrireAbonnement", e);
    } finally {
        try { if (pstmtUpdateOldSub != null) pstmtUpdateOldSub.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (pstmtInsertNewSub != null) pstmtInsertNewSub.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (pstmtUpdateSolde != null) pstmtUpdateSolde.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (pstmtInsertPayment != null) pstmtInsertPayment.close(); } catch (SQLException e) { e.printStackTrace(); } // Close the new PreparedStatement
        if (conn != null) {
            try {
                conn.setAutoCommit(true); // Reset auto-commit state
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}


     private void loadClientData() {
        if (this.clientEmail == null || this.clientEmail.isEmpty()) {
            //JOptionPane.showMessageDialog(this, "Email du client non fourni.", "Erreur", JOptionPane.ERROR_MESSAGE);
            System.err.println("Client email not provided to AcceuilClient. Cannot load data.");
            // Optionally disable fields or show a message in the UI
            jTextField1.setText("");
            jTextField2.setText("");
            jTextField3.setText("");
            jTextField4.setText("");
            jTextField5.setText("");
            jTextField6.setText(""); // Clear password field
            jComboBox3.setSelectedIndex(0);
            // You might want to disable the "Enregister" button as well
            // jButton2.setEnabled(false);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Replace with your actual database connection details
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "votre_nouveau_mot_de_passe");
            String sql = "SELECT Nom, Prenom, Adresse, `Date De Naissance`, Email, `Mot de passe`, Sexe FROM client WHERE Email = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, this.clientEmail);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                jTextField1.setText(rs.getString("Nom"));
                jTextField2.setText(rs.getString("Prenom"));
                jTextField3.setText(rs.getString("Adresse"));
                jTextField4.setText(rs.getString("Date De Naissance")); // Ensure this matches DB column name
                jTextField5.setText(rs.getString("Email"));
                jTextField6.setText(rs.getString("Mot de passe")); // SECURITY RISK: Loading plain text password
                
                String sexe = rs.getString("Sexe");
                if ("Male".equalsIgnoreCase(sexe)) {
                    jComboBox3.setSelectedItem("Male");
                } else if ("Female".equalsIgnoreCase(sexe)) {
                    jComboBox3.setSelectedItem("Female");
                } else {
                    jComboBox3.setSelectedIndex(0); // Default or handle other cases
                }
            } else {
                JOptionPane.showMessageDialog(this, "Client non trouvé dans la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
                // Clear fields if client not found
                jTextField1.setText("");
                jTextField2.setText("");
                jTextField3.setText("");
                jTextField4.setText("");
                jTextField5.setText("");
                jTextField6.setText("");
                jComboBox3.setSelectedIndex(0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données lors du chargement des informations: " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }


    
    private boolean hasActiveSubscription() throws SQLException {
        // ... (Keep this method as is, it manages its own connection)
        if (this.clientId == 0) return false;
        Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null; boolean activeSubExists = false;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "votre_nouveau_mot_de_passe");
            String sql = "SELECT COUNT(*) as active_count FROM abonnement WHERE client_id = ? AND Status = 'actif' AND `Date du fin` >= CURDATE()";
            pstmt = conn.prepareStatement(sql); pstmt.setInt(1, this.clientId);
            rs = pstmt.executeQuery();
            if (rs.next()) { if (rs.getInt("active_count") > 0) { activeSubExists = true; } }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return activeSubExists;
    }

    
    private void loadPaymentHistory() {
    if (this.clientId == 0) {
        // Clear table if no client is loaded or an error occurred earlier
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Clear existing rows
        // Optionally display a message in the table or a label
        return;
    }

    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.setRowCount(0); // Clear existing rows before loading new ones

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    // Formatters for displaying date and time nicely in the table
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    try {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "votre_nouveau_mot_de_passe");
        // Select payment history for the current client, order by most recent first
        String sql = "SELECT id, Client, montant, date, heure FROM paiment WHERE Client = ? ORDER BY date DESC, heure DESC";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, this.clientId);
        rs = pstmt.executeQuery();

        while (rs.next()) {
            int transactionId = rs.getInt("id");
            int clientIdFromDb = rs.getInt("Client"); // Could be used for verification or display if needed
            int montant = rs.getInt("montant");
            java.sql.Date dateSql = rs.getDate("date");
            java.sql.Time timeSql = rs.getTime("heure");

            String formattedDate = (dateSql != null) ? dateSql.toLocalDate().format(dateFormatter) : "N/A";
            String formattedTime = (timeSql != null) ? timeSql.toLocalTime().format(timeFormatter) : "N/A";

            // Add row to the table model
            // Column order: "id transaction", "Client", "Montant", "Date", "heure"
            model.addRow(new Object[]{
                transactionId,
                clientIdFromDb, // You might want to show client name instead, requires a JOIN or another lookup
                montant,
                formattedDate,
                formattedTime
            });
        }

        if (model.getRowCount() == 0) {
            // Optionally, display a message if no payment history is found
            // For example, add a single row with a message, or update a separate label.
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Erreur lors du chargement de l'historique des paiements: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(AcceuilClient.class.getName()).log(Level.SEVERE, "Error loading payment history", e);
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator3 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        tabbedPaneCustom1 = new raven.tabbed.TabbedPaneCustom();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel16 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        tabbedPaneCustom1.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        tabbedPaneCustom1.setUnselectedColor(new java.awt.Color(247, 191, 191));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Bienvenue de nouveau !");

        jPanel16.setBackground(new java.awt.Color(204, 204, 204));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel40.setText("Mon Solde");

        jLabel41.setText("jLabel41");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel41)
                    .addComponent(jLabel40))
                .addContainerGap(1016, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel40)
                .addGap(18, 18, 18)
                .addComponent(jLabel41)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 847, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1085, 1085, 1085)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(280, 280, 280)
                                .addComponent(jLabel1))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 836, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(126, 126, 126)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(186, Short.MAX_VALUE))
        );

        tabbedPaneCustom1.addTab("Acceuil", jPanel2);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel6.setText("Mes Informations Personelles");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel7.setText("Nom ");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel8.setText("Penom ");

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel9.setText("Addresse");

        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel10.setText("Date De Naissance");

        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel11.setText("e-mail");

        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel12.setText("Mot de passe");

        jButton1.setText("Annuler");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Enregister");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel34.setText("Sexe");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(332, 332, 332)
                        .addComponent(jLabel6))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(211, 211, 211)))))
                .addContainerGap(489, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(33, 33, 33)
                .addComponent(jButton2)
                .addGap(210, 210, 210))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel6)
                .addGap(76, 76, 76)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 104, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2))
                        .addGap(96, 96, 96))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        tabbedPaneCustom1.addTab("Mon Profil", jPanel3);

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel13.setText("Abonnement Actuel");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel14.setText("Type :");

        jLabel15.setText("exemple Type");

        jLabel17.setText("exemple Type");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel18.setText("Date du debut :");

        jLabel19.setText("exemple Type");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel20.setText("Date du fin :");

        jLabel21.setText("exemple Type");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel22.setText("Status :");

        jButton3.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jButton3.setText("Renouvler");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel35.setText("Prix : ");

        jLabel36.setText("jLabel36");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)))
                        .addContainerGap(956, Short.MAX_VALUE))))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(jButton3)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(jLabel36))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel17))
                .addGap(4, 4, 4)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jLabel21))
                .addGap(22, 22, 22)
                .addComponent(jLabel16)
                .addGap(18, 18, 18)
                .addComponent(jButton3)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        jLabel23.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel23.setText("Liste des abonnement : ");

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/30-jours.png"))); // NOI18N

        jButton4.setText("Inscrire");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/7-jours (1).png"))); // NOI18N

        jButton5.setText("Inscrire");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/happy-new-year.png"))); // NOI18N

        jButton6.setText("Inscrire");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel37.setText("800 DA");

        jLabel38.setText("2000 Da");
        jLabel38.setToolTipText("");

        jLabel39.setText("17000 DA");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(373, 373, 373)
                                .addComponent(jLabel23))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(90, 90, 90)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jButton4)
                                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(225, 225, 225)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel24)
                                    .addComponent(jButton5))
                                .addGap(224, 224, 224)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton6)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel39)
                                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 380, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(118, 118, 118)
                .addComponent(jLabel37)
                .addGap(247, 247, 247)
                .addComponent(jLabel38)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(39, 39, 39)
                            .addComponent(jLabel26))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel24))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel38)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton5)
                    .addComponent(jButton6))
                .addContainerGap(94, Short.MAX_VALUE))
        );

        tabbedPaneCustom1.addTab("Mes Abonnement", jPanel4);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1153, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 559, Short.MAX_VALUE)
        );

        tabbedPaneCustom1.addTab("Messagerie", jPanel7);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));

        jLabel32.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel32.setText("Historique");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "id transaction", "Client", "Montant", "Date", "heure"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(307, 307, 307)
                        .addComponent(jLabel32))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 625, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(151, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(249, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPaneCustom1.addTab("Historique des payement", jPanel8);

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel33.setText("Voullez vous vraiment se deconnecter ?");

        jButton8.setText("Se Deconnecter");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(289, 289, 289)
                        .addComponent(jLabel33))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(379, 379, 379)
                        .addComponent(jButton8)))
                .addContainerGap(601, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addComponent(jLabel33)
                .addGap(18, 18, 18)
                .addComponent(jButton8)
                .addContainerGap(409, Short.MAX_VALUE))
        );

        tabbedPaneCustom1.addTab("Déconnexion", jPanel9);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPaneCustom1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPaneCustom1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        //Déconnexion
        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment vous déconnecter ?", "Confirmation de déconnexion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Déconnexion réussie.");
            dispose();
            new LoginScreen("Client").setVisible(true);
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        inscrireAbonnement("annuel", 17000, 365); // Adjust price and duration if needed
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        inscrireAbonnement("mensuel", 2000, 30);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        inscrireAbonnement("hebdomadaire", 800, 7);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        // Renouvler Abonnement
        if (currentActiveSubDetails != null) {
            // prix is already int in currentActiveSubDetails
            if (this.clientSolde < currentActiveSubDetails.prix) { // Direct int comparison
                JOptionPane.showMessageDialog(this,
                    "Solde insuffisant pour renouveler.\nSolde actuel: " + this.clientSolde + " DA.\n" +
                    "Prix du renouvellement: " + currentActiveSubDetails.prix + " DA.",
                    "Solde Insuffisant", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int newSoldeCalculated = this.clientSolde - currentActiveSubDetails.prix;
            int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous renouveler votre abonnement " + currentActiveSubDetails.type +
                " pour " + currentActiveSubDetails.prix + " DA (durée: " + currentActiveSubDetails.originalDurationDays + " jours) à partir d'aujourd'hui?\n" +
                "Solde actuel: " + this.clientSolde + " DA.\n" +
                "Nouveau solde après opération: " + newSoldeCalculated + " DA.",
                "Confirmation de renouvellement", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                inscrireAbonnement(currentActiveSubDetails.type, currentActiveSubDetails.prix, currentActiveSubDetails.originalDurationDays);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Aucun abonnement actif à renouveler. Veuillez choisir un nouvel abonnement.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        // Logic for "Enregister" (Save) button
        String nom = jTextField1.getText();
        String prenom = jTextField2.getText();
        String adresse = jTextField3.getText();
        String dateNaissance = jTextField4.getText(); // Add validation for date format if necessary
        String newEmail = jTextField5.getText();
        String motDePasse = jTextField6.getText(); // SECURITY RISK: Saving plain text password
        String sexe = jComboBox3.getSelectedItem().toString();

        // Basic validation (add more as needed)
        if (nom.isEmpty() || prenom.isEmpty() || newEmail.isEmpty() || motDePasse.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires (Nom, Prénom, Email, Mot de passe).", "Champs Requis", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "votre_nouveau_mot_de_passe");
            // It's safer to update based on a non-editable primary key if available.
            // Here, we use the original email (this.clientEmail) to identify the record.
            String sql = "UPDATE client SET Nom = ?, Prenom = ?, Adresse = ?, `Date De Naissance` = ?, Email = ?, `Mot de passe` = ?, Sexe = ? WHERE Email = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, adresse);
            pstmt.setString(4, dateNaissance);
            pstmt.setString(5, newEmail);
            pstmt.setString(6, motDePasse); // Storing plain text password
            pstmt.setString(7, sexe);
            pstmt.setString(8, this.clientEmail); // Use the original email to find the record

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Informations mises à jour avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                // If the email was changed, update the local clientEmail variable
                if (!this.clientEmail.equals(newEmail)) {
                    this.clientEmail = newEmail;
                    setTitle("Accueil Client - " + this.clientEmail); // Update window title
                }
            } else {
                JOptionPane.showMessageDialog(this, "Aucune modification effectuée ou client non trouvé.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données lors de la mise à jour: " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        //  Annuler (Mon Profil)
        int confirm = JOptionPane.showConfirmDialog(this,
            "Voulez-vous annuler les modifications et recharger les données d'origine ?",
            "Confirmer l'annulation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Reload both client profile and subscription data
                loadClientDataAndSubscription();
            } catch (SQLException ex) {
                Logger.getLogger(AcceuilClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

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
            java.util.logging.Logger.getLogger(AcceuilClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AcceuilClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AcceuilClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AcceuilClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AcceuilClient().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private raven.tabbed.TabbedPaneCustom tabbedPaneCustom1;
    // End of variables declaration//GEN-END:variables

   
}
