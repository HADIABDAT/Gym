package DAO;

import gym.entities.Abonnement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbonnementDAO {

    private Connection getConnection() throws SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/gym";
        String user = "root";
        String pwd = "Votre_nouveau_motdepasse"; // Replace with your actual password
        return DriverManager.getConnection(dbUrl, user, pwd);
    }

    public List<Abonnement> getAllAbonnements() throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM abonnement");

            while (rs.next()) {
                Abonnement abonnement = new Abonnement();
                abonnement.setId(rs.getInt("id"));
                abonnement.setType(rs.getString("Type"));
                abonnement.setDateDeDebut(rs.getDate("Date de debut"));
                abonnement.setDateDuFin(rs.getDate("Date du fin"));
                abonnement.setPrix(rs.getInt("Prix"));
                abonnement.setStatus(rs.getString("Status"));
                abonnements.add(abonnement);
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        return abonnements;
    }

    public Abonnement getAbonnementById(int id) throws SQLException {
        Abonnement abonnement = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM abonnement WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                abonnement = new Abonnement();
                abonnement.setId(rs.getInt("id"));
                abonnement.setType(rs.getString("Type"));
                abonnement.setDateDeDebut(rs.getDate("Date de debut"));
                abonnement.setDateDuFin(rs.getDate("Date du fin"));
                abonnement.setPrix(rs.getInt("Prix"));
                abonnement.setStatus(rs.getString("Status"));
            }
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return abonnement;
    }
    

    public void addAbonnement(Abonnement abonnement) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "INSERT INTO abonnement (Type, `Date de debut`, `Date du fin`, Prix, Status) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, abonnement.getType());
            pstmt.setDate(2, new java.sql.Date(abonnement.getDateDeDebut().getTime())); // Convert to java.sql.Date
            pstmt.setDate(3, new java.sql.Date(abonnement.getDateDuFin().getTime()));   // Convert to java.sql.Date
            pstmt.setInt(4, abonnement.getPrix());
            pstmt.setString(5, abonnement.getStatus());
            pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public void updateAbonnement(Abonnement abonnement) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "UPDATE abonnement SET Type = ?, `Date de debut` = ?, `Date du fin` = ?, Prix = ?, Status = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, abonnement.getType());
            pstmt.setDate(2, new java.sql.Date(abonnement.getDateDeDebut().getTime()));  // Convert
            pstmt.setDate(3, new java.sql.Date(abonnement.getDateDuFin().getTime()));  // Convert
            pstmt.setInt(4, abonnement.getPrix());
            pstmt.setString(5, abonnement.getStatus());
            pstmt.setInt(6, abonnement.getId());
            pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public void deleteAbonnement(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "DELETE FROM abonnement WHERE id = ?";
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