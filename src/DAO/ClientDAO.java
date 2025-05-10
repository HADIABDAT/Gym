package DAO;

import gym.entities.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    private Connection getConnection() throws SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/gym";
        String user = "root";
        String pwd = "votre_nouveau_mot_de_passe"; // Replace with your actual password
        return DriverManager.getConnection(dbUrl, user, pwd);
    }

    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM client");

            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getInt("id"));
                client.setNom(rs.getString("Nom"));
                client.setPrenom(rs.getString("Prenom"));
                client.setAdresse(rs.getString("Adresse"));
                client.setDateDeNaissance(rs.getString("date de naissance"));
                client.setNTel(rs.getString("N tel"));
                client.setEmail(rs.getString("Email"));
                client.setMotDePasse(rs.getString("Mot de passe"));
                client.setSexe(rs.getString("sexe"));
                clients.add(client);
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        return clients;
    }
    
     public Client getClientById(int id) throws SQLException {
        Client client = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM client WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                client = new Client();
                 client.setId(rs.getInt("id"));
                client.setNom(rs.getString("Nom"));
                client.setPrenom(rs.getString("Prenom"));
                client.setAdresse(rs.getString("Adresse"));
                client.setDateDeNaissance(rs.getString("date de naissance"));
                client.setNTel(rs.getString("N tel"));
                client.setEmail(rs.getString("Email"));
                client.setMotDePasse(rs.getString("Mot de passe"));
                client.setSexe(rs.getString("sexe"));
            }
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return client;
    }

    public void addClient(Client client) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "INSERT INTO client (Nom, Prenom, Adresse, `date de naissance`, `N tel`, Email, `Mot de passe`, sexe) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getAdresse());
            pstmt.setString(4, client.getDateDeNaissance());
            pstmt.setString(5, client.getNTel());
            pstmt.setString(6, client.getEmail());
            pstmt.setString(7, client.getMotDePasse());
            pstmt.setString(8, client.getSexe());
            pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public void updateClient(Client client) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "UPDATE client SET Nom = ?, Prenom = ?, Adresse = ?, `date de naissance` = ?, `N tel` = ?, Email = ?, `Mot de passe` = ?, sexe = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getAdresse());
            pstmt.setString(4, client.getDateDeNaissance());
            pstmt.setString(5, client.getNTel());
            pstmt.setString(6, client.getEmail());
            pstmt.setString(7, client.getMotDePasse());
            pstmt.setString(8, client.getSexe());
            pstmt.setInt(9, client.getId());
            pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public void deleteClient(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "DELETE FROM client WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    private void closeResources(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
        if (rs != null) try { rs.close(); } catch (SQLException e) {}
        if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
        if (conn != null) try { conn.close(); } catch (SQLException e) {}
    }
}