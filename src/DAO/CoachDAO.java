package DAO;

import gym.entities.Coach;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoachDAO {

    private Connection getConnection() throws SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/gym";
        String user = "root";
        String pwd = "votre_nouveau_mot_de_passe"; // Replace with your actual password
        return DriverManager.getConnection(dbUrl, user, pwd);
    }

    public List<Coach> getAllCoaches() throws SQLException {
        List<Coach> coaches = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM coach");

            while (rs.next()) {
                Coach coach = new Coach();
                coach.setId(rs.getInt("id"));
                coach.setNom(rs.getString("Nom"));
                coach.setPrenom(rs.getString("Prenom"));
                coach.setAdresse(rs.getString("Adresse"));
                coach.setNTel(rs.getString("N tel"));
                coach.setEtat(rs.getString("Etat"));
                coaches.add(coach);
            }
        } finally {
           closeResources(conn, stmt, rs);
        }
        return coaches;
    }
    
    public Coach getCoachById(int id) throws SQLException{
        Coach coach = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
         try {
            conn = getConnection();
            String sql = "SELECT * FROM coach WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                coach = new Coach();
                coach.setId(rs.getInt("id"));
                coach.setNom(rs.getString("Nom"));
                coach.setPrenom(rs.getString("Prenom"));
                coach.setAdresse(rs.getString("Adresse"));
                coach.setNTel(rs.getString("N tel"));
                coach.setEtat(rs.getString("Etat"));
            }
         }finally{
             closeResources(conn, pstmt, rs);
         }
         return coach;
    }

    public void addCoach(Coach coach) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "INSERT INTO coach (Nom, Prenom, Adresse, `N tel`, Etat) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, coach.getNom());
            pstmt.setString(2, coach.getPrenom());
            pstmt.setString(3, coach.getAdresse());
            pstmt.setString(4, coach.getNTel());
            pstmt.setString(5, coach.getEtat());
            pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public void updateCoach(Coach coach) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "UPDATE coach SET Nom = ?, Prenom = ?, Adresse = ?, `N tel` = ?, Etat = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, coach.getNom());
            pstmt.setString(2, coach.getPrenom());
            pstmt.setString(3, coach.getAdresse());
            pstmt.setString(4, coach.getNTel());
            pstmt.setString(5, coach.getEtat());
            pstmt.setInt(6, coach.getId());
            pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public void deleteCoach(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "DELETE FROM coach WHERE id = ?";
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