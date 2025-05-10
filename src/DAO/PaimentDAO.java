package DAO;

import gym.entities.Paiment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaimentDAO {

    private Connection getConnection() throws SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/gym";
        String user = "root";
        String pwd = "votre_nouveau_mot_de_passe";  // Replace
        return DriverManager.getConnection(dbUrl, user, pwd);
    }

    public List<Paiment> getAllPaiments() throws SQLException {
        List<Paiment> paiments = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM paiment");

            while (rs.next()) {
                Paiment paiment = new Paiment();
                paiment.setId(rs.getInt("id"));
                paiment.setClientId(rs.getInt("Client"));
                paiment.setMontant(rs.getInt("montant"));
                paiment.setDate(rs.getDate("date"));
                paiments.add(paiment);
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        return paiments;
    }
    
    public Paiment getPaimentById(int id) throws SQLException{
        Paiment paiment = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try{
            conn = getConnection();
            String sql = "SELECT * FROM paiment WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if(rs.next()){
                 paiment = new Paiment();
                paiment.setId(rs.getInt("id"));
                paiment.setClientId(rs.getInt("Client"));
                paiment.setMontant(rs.getInt("montant"));
                paiment.setDate(rs.getDate("date"));
            }
            
        }finally{
            closeResources(conn, pstmt, rs);
        }
        return paiment;
    }

    public void addPaiment(Paiment paiment) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "INSERT INTO paiment (Client, montant, date) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, paiment.getClientId());
            pstmt.setInt(2, paiment.getMontant());
            pstmt.setDate(3, new java.sql.Date(paiment.getDate().getTime())); // Convert
            pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public void updatePaiment(Paiment paiment) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "UPDATE paiment SET Client = ?, montant = ?, date = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, paiment.getClientId());
            pstmt.setInt(2, paiment.getMontant());
            pstmt.setDate(3, new java.sql.Date(paiment.getDate().getTime())); //convert
            pstmt.setInt(4, paiment.getId());
            pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public void deletePaiment(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "DELETE FROM paiment WHERE id = ?";
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