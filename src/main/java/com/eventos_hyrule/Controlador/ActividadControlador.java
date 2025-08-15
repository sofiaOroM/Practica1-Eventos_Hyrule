/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.Controlador;

import com.eventos_hyrule.funciones.Actividad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author sofia
 */
public class ActividadControlador {
    private Connection connection;

    public ActividadControlador(Connection connection) {
        this.connection = connection;
    }

    // Método para insertar una nueva actividad
    public boolean insertarActividad(Actividad actividad) throws SQLException {
        // Validar que el encargado no sea un asistente
        if (esParticipanteAsistente(actividad.getEmailEncargado())) {
            throw new SQLException("El encargado debe ser CONFERENCISTA, TALLERISTA u OTRO");
        }
        
        String sql = "INSERT INTO ACTIVIDAD (codigo_actividad, codigo_evento, tipo_actividad, titulo_sctividad, " +
                     "email_encargado, hora_inicio, hora_fin, cupo_maximo_actividad) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, actividad.getCodigoActividad());
            pstmt.setString(2, actividad.getCodigoEvento());
            pstmt.setString(3, actividad.getTipoActividad().name());
            pstmt.setString(4, actividad.getTituloActividad());
            pstmt.setString(5, actividad.getEmailEncargado());
            pstmt.setTime(6, actividad.getHoraInicio());
            pstmt.setTime(7, actividad.getHoraFin());
            pstmt.setInt(8, actividad.getCupoMaximo());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private boolean esParticipanteAsistente(String email) throws SQLException {
        String sql = "SELECT tipo_inscripcion FROM INSCRIPCION WHERE email_participante = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo_inscripcion");
                    return "ASISTENTE".equals(tipo);
                }
                throw new SQLException("El participante no está inscrito en ningún evento");
            }
        }
    }
    
    // Método para verificar si existe una actividad por su código
    public boolean existeActividad(String codigoActividad) throws SQLException {
        String sql = "SELECT 1 FROM ACTIVIDAD WHERE codigo_actividad = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, codigoActividad);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Método para obtener todas las actividades (para combobox)
    public List<Actividad> listarActividades() throws SQLException {
        List<Actividad> actividades = new ArrayList<>();
        String sql = "SELECT * FROM ACTIVIDAD";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                actividades.add(new Actividad(
                    rs.getString("codigo_actividad"),
                    rs.getString("codigo_evento"),
                    Actividad.TipoActividad.valueOf(rs.getString("tipo_actividad")),
                    rs.getString("titulo_actividad"),
                    rs.getString("email_encargado"),
                    rs.getTime("hora_inicio"),
                    rs.getTime("hora_fin"),
                    rs.getInt("cupo_maximo_actividad")
                ));
            }
        }
        return actividades;
    }

    // Método para obtener actividades por evento (para reportes)
    public List<Actividad> listarActividadesPorEvento(String codigoEvento) throws SQLException {
        List<Actividad> actividades = new ArrayList<>();
        String sql = "SELECT * FROM ACTIVIDAD WHERE codigo_evento = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, codigoEvento);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    actividades.add(new Actividad(
                        rs.getString("codigo_actividad"),
                        codigoEvento,
                        Actividad.TipoActividad.valueOf(rs.getString("tipo_actividad")),
                        rs.getString("titulo_actividad"),
                        rs.getString("email_encargado"),
                        rs.getTime("hora_inicio"),
                        rs.getTime("hora_fin"),
                        rs.getInt("cupo_maximo_actividad")
                    ));
                }
            }
        }
        return actividades;
    }

    // Método para obtener los encargados disponibles (no asistentes)
    public List<String[]> listarEncargados() throws SQLException {
        List<String[]> encargados = new ArrayList<>();
        String sql = "SELECT i.email_participante, p.nombre_completo " +
                     "FROM PARTICIPANTE p " +
                     "JOIN INSCRIPCION i ON p.email_participante = i.email_participante " +
                     "WHERE i.tipo_inscripcion != 'ASISTENTE'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                encargados.add(new String[]{
                    rs.getString("email_participante"),
                    rs.getString("nombre_completo")
                });
            }
        }
        return encargados;
    }

    // Método para obtener eventos disponibles (para combobox)
    public List<String[]> listarEventos() throws SQLException {
        List<String[]> eventos = new ArrayList<>();
        String sql = "SELECT codigo_evento, titulo_evento FROM EVENTO ORDER BY fecha_evento";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                eventos.add(new String[]{
                    rs.getString("codigo_evento"),
                    rs.getString("titulo_evento")
                });
            }
        }
        return eventos;
    }    
}
