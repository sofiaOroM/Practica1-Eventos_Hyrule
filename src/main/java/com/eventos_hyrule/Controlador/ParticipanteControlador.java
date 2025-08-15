/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.Controlador;

import com.eventos_hyrule.funciones.Participante;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author sofia
 */
public class ParticipanteControlador {
    private Connection connection;

    public ParticipanteControlador(Connection connection) {
        this.connection = connection;
    }

    public boolean insertarParticipante(Participante participante) throws SQLException {
        String sql = "INSERT INTO PARTICIPANTE (email_participante, nombre_completo, tipo_participante, institucion) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            pstmt.setString(1, participante.getEmailParticipante());
            pstmt.setString(2, participante.getNombreCompleto());
            pstmt.setString(3, participante.getTipoParticipante().name());
            pstmt.setString(4, participante.getInstitucion());
            
            int result = pstmt.executeUpdate();
            connection.commit();
            return result > 0;
        } catch (SQLException e){
            connection.rollback();
            throw e;
        } finally{
            connection.setAutoCommit(true);
        }
    }

    public List<Participante> listarParticipantes() throws SQLException {
        List<Participante> participantes = new ArrayList<>();
        String sql = "SELECT email_participante, nombre_completo, tipo_participante, institucion FROM PARTICIPANTE";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                participantes.add(new Participante(
                    rs.getString("email_participante"),
                    rs.getString("nombre_completo"),
                    Participante.TipoParticipante.valueOf(rs.getString("tipo_participante")),
                    rs.getString("institucion")
                ));
            }
        }
        return participantes;
    }

    public List<Participante> listarNoAsistentes() throws SQLException {
        List<Participante> participantes = new ArrayList<>();
        String sql = "SELECT p.email_participante, p.nombre_completo, p.tipo_participante, p.institucion " +
                     "FROM PARTICIPANTE p " +
                     "JOIN INSCRIPCION i ON p.email_participante = i.email_participante " +
                     "WHERE i.tipo_inscripcion != 'ASISTENTE'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                participantes.add(new Participante(
                    rs.getString("email_participante"),
                    rs.getString("nombre_completo"),
                    Participante.TipoParticipante.valueOf(rs.getString("tipo_participante")),
                    rs.getString("institucion")
                ));
            }
        }
        return participantes;
    }

    public Participante buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT email_participante, nombre_completo, tipo_participante, institucion " +
                     "FROM PARTICIPANTE WHERE email_participante = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Participante(
                        rs.getString("email_participante"),
                        rs.getString("nombre_completo"),
                        Participante.TipoParticipante.valueOf(rs.getString("tipo_participante")),
                        rs.getString("institucion")
                    );
                }
                return null;
            }
        }
    }

    public boolean existeParticipante(String email) throws SQLException {
        String sql = "SELECT 1 FROM PARTICIPANTE WHERE email_participante = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
