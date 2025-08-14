/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.Controlador;


import com.eventos_hyrule.funciones.Asistencia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author sofia
 */
public class AsistenciaControlador {
    private Connection connection;

    public AsistenciaControlador(Connection connection) {
        this.connection = connection;
    }

    public boolean registrarAsistencia(Asistencia asistencia) throws SQLException {
        // Primero verificamos si ya existe una asistencia igual
        if (existeAsistencia(asistencia.getEmailParticipante(), asistencia.getCodigoActividad())) {
            return false;
        }

        String sql = "INSERT INTO ASISTENCIA (email_participante, codigo_actividad, fecha_asistencia) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, asistencia.getEmailParticipante());
            pstmt.setString(2, asistencia.getCodigoActividad());
            pstmt.setTimestamp(3, asistencia.getFechaAsistencia());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    private boolean existeAsistencia(String emailParticipante, String codigoActividad) throws SQLException {
        String sql = "SELECT 1 FROM ASISTENCIA WHERE email_participante = ? AND codigo_actividad = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emailParticipante);
            pstmt.setString(2, codigoActividad);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int contarAsistenciasPorActividad(String codigoActividad) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ASISTENCIA WHERE codigo_actividad = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, codigoActividad);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }    
}
