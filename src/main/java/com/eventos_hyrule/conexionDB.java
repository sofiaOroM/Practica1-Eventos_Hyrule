/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 *
 * @author sofia
 */
public class conexionDB {
    private static final String IP = "localhost";
    private static final int PUERTO = 3306;
    private static final String SCHEMA = "eventos_Hyrule";
    private static final String USER_NAME = "admin";
    private static final String PASSWORD = "admin1";
    
    private static final String URL = "jdbc:mysql://" +
            IP + ":" + PUERTO + "/" + SCHEMA;
    private Connection connection;
    
    public void Connect(){
        System.out.println("URL de conexion: " + URL);
        try {
            connection = DriverManager.getConnection(URL,USER_NAME, PASSWORD);
            System.out.println("Esquema:" + connection.getSchema());
            System.out.println("Catalogo:" + connection.getCatalog());
            
        } catch (Exception e) {
            System.out.println("Error de conexion");
            e.printStackTrace();
            
        }
    }
    
}
