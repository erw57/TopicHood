/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.topichood.dbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wanger
 */
public class Dbcon {
    private static final String DBDRIVER="com.mysql.jdbc.Driver";
    private static final String DBURL="jdbc:mysql://angchen.cu.cc:3306/travelpal";
    private static final String DBUSER="angchen";
    private static final String DBPASSWORD="angchen";
    private Connection conn=null;
    
    public Dbcon(){
        try {
            Class.forName(DBDRIVER);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dbcon.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            this.conn=DriverManager.getConnection(DBURL,DBUSER,DBPASSWORD);
        } catch (SQLException ex) {
            Logger.getLogger(Dbcon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void close(){
        if(this.conn!=null){
            try {
                this.conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Dbcon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }       
    }
    
    public Connection getConnection(){
        return this.conn;
    }
}
