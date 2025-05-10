/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Jdbc;

import java.sql.*;

/**
 *
 * @author Chen Zen
 */
public class Jdbc {
    public static void main(String[] args) throws SQLException {
        Connection MyConn = null ;
        Statement Mystmt = null ;
        ResultSet MyRs = null ;
        
        String dbUrl = "jdbc:mysql://localhost:3306/gym" ;
        String user = "root";
        String pwd = "votre_nouveau_mot_de_passe";
        
        try{
            // 1. get a connection to the dtatabse 
            MyConn = DriverManager.getConnection(dbUrl,user,pwd);
            
            // 2. create a statement 
            Mystmt= MyConn.createStatement();
            
            // 3. Execute Sql querie
            MyRs = Mystmt.executeQuery("select * from client");
            
            // 4. process the result set
            while ( MyRs.next()){
              System.out.println(MyRs.getString("Nom"));
            }
            
       
        }catch (Exception e){
            
        }
    }
}
