package DAO;

import gym.entities.Seance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeanceDAO {

    private Connection getConnection() throws SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/gym";
        String user = "root";
        String pwd = "votre_nouveau_mot_de_passe";  // Replace
        return DriverManager.getConnection(dbUrl, user, pwd);
    }

    public List<Seance> getAllSeances() throws SQLException {
        List<Seance> seances = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM seance");

            while (rs.next()) {
                Seance seance = new Seance();
                seance.setId(rs.getInt("id"));
                seance.setType(rs.getString("type"));
                seance.setHeure(rs.getString("Heure"));
                seance.setDate(rs.getDate("Date"));
                seance.setCoachId(rs.getInt("Coach"));
                seances.add(seance);
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        return seances;
    }
    
    public Seance getSeanceById(int id) throws SQLException{
        Seance seance = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try{
            conn = getConnection();
            String sql = "SELECT * FROM seance WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if(rs.next()){
                 seance = new Seance();
                seance.setId(rs.getInt("id"));
                seance.setType(rs.getString("type"));
                seance.setHeure(rs.getString("Heure"));
                seance.setDate(rs.getDate("Date"));
                seance.setCoachId(rs.getInt("Coach"));
            }
            
        }finally{
            closeResources(conn, pstmt, rs);
        }
        return seance;
    }

    public void addSeance(Seance seance) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "INSERT INTO seance (type, Heure, Date, Coach) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, seance.getType());
            pstmt.setString(2, seance.getHeure());
            pstmt.setDate(3, new java.sql.Date(seance.getDate().getTime()));  // Convert
            pstmt.setInt(4, seance.getCoachId());
            pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public void updateSeance(Seance seance) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "UPDATE seance SET type = ?, Heure = ?, Date = ?, Coach = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, seance.getType());
            pstmt.setString(2, seance.getHeure());
            pstmt.setDate(3, new java.sql.Date(seance.getDate().getTime()));  // Convert
            pstmt.setInt(4, seance.getCoachId());
            pstmt.setInt(5, seance.getId());
            pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public void deleteSeance(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "DELETE FROM seance WHERE id = ?";
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