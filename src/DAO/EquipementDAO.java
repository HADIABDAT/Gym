package DAO;

import gym.entities.Equipement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipementDAO {

    private Connection getConnection() throws SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/gym";
        String user = "root";
        String pwd = "votre_nouveau_mot_de_passe"; // Replace with your actual password
        return DriverManager.getConnection(dbUrl, user, pwd);
    }

    public List<Equipement> getAllEquipements() throws SQLException {
        List<Equipement> equipements = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM euipement"); // Corrected table name

            while (rs.next()) {
                Equipement equipement = new Equipement();
                equipement.setId(rs.getInt("id"));
                equipement.setNom(rs.getString("Nom"));
                equipement.setType(rs.getString("Type"));
                equipement.setMarque(rs.getString("Marque"));
                equipement.setStatus(rs.getString("Status"));
                equipements.add(equipement);
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        return equipements;
    }
    
    public Equipement getEquipementById(int id) throws SQLException{
        Equipement equipement = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try{
            conn = getConnection();
            String sql = "SELECT * FROM euipement WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
             if (rs.next()) {
                equipement = new Equipement();
                equipement.setId(rs.getInt("id"));
                equipement.setNom(rs.getString("Nom"));
                equipement.setType(rs.getString("Type"));
                equipement.setMarque(rs.getString("Marque"));
                equipement.setStatus(rs.getString("Status"));
            }
            
        }finally{
            closeResources(conn, pstmt, rs);
        }
        return equipement;
    }

    public void addEquipement(Equipement equipement) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "INSERT INTO euipement (Nom, Type, Marque, Status) VALUES (?, ?, ?, ?)"; // Corrected table name
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, equipement.getNom());
            pstmt.setString(2, equipement.getType());
            pstmt.setString(3, equipement.getMarque());
            pstmt.setString(4, equipement.getStatus());
            pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public void updateEquipement(Equipement equipement) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "UPDATE euipement SET Nom = ?, Type = ?, Marque = ?, Status = ? WHERE id = ?"; // Corrected table name
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, equipement.getNom());
            pstmt.setString(2, equipement.getType());
            pstmt.setString(3, equipement.getMarque());
            pstmt.setString(4, equipement.getStatus());
            pstmt.setInt(5, equipement.getId());
            pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public void deleteEquipement(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "DELETE FROM euipement WHERE id = ?"; // Corrected table name
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