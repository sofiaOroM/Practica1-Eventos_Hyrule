/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.Controlador;

import com.eventos_hyrule.conexionDB;
import com.eventos_hyrule.funciones.Certificado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author sofia
 */
public class CertificadoControlador {
    private final Connection connection;
    
    public CertificadoControlador(Connection connection) {
        conexionDB conexion = new conexionDB();
        conexion.Connect();
        this.connection = conexion.getConnection();
    }

    /**
     * Obtiene la lista de eventos que tienen certificados generados
     */
    public List<String> obtenerEventosConCertificados() throws SQLException {
        List<String> eventos = new ArrayList<>();
        String sql = "SELECT DISTINCT codigo_evento FROM evento ORDER BY codigo_evento";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                eventos.add(rs.getString("codigo_evento"));
            }
        }
        
        return eventos;
    }
    
    /**
     * Busca certificados según criterios
     */
    public List<Certificado> buscarCertificados(String email, String codigoEvento) throws SQLException {
        List<Certificado> certificados = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT c.*, p.nombre_completo, e.titulo_evento " +
            "FROM certificado c " +
            "JOIN participante p ON c.email_participante = p.email_participante " +
            "JOIN evento e ON c.codigo_evento = e.codigo_evento " +
            "WHERE 1=1"
        );
        
        if (email != null && !email.isEmpty()) {
            sql.append(" AND c.email_participante LIKE ?");
        }
        if (codigoEvento != null && !codigoEvento.isEmpty()) {
            sql.append(" AND c.codigo_evento = ?");
        }
        sql.append(" ORDER BY c.fecha_emision DESC");
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (email != null && !email.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + email + "%");
            }
            if (codigoEvento != null && !codigoEvento.isEmpty()) {
                pstmt.setString(paramIndex++, codigoEvento);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Certificado cert = new Certificado();
                    cert.setIdCertificado(rs.getInt("id_certificado"));
                    cert.setEmailParticipante(rs.getString("email_participante"));
                    cert.setCodigoEvento(rs.getString("codigo_evento"));
                    cert.setFechaEmision(rs.getTimestamp("fecha_emision"));
                    cert.setRutaArchivo(rs.getString("ruta_archivo"));
                    cert.setNombreParticipante(rs.getString("nombre_completo"));
                    cert.setTituloEvento(rs.getString("titulo_evento"));
                    
                    certificados.add(cert);
                }
            }
        }
        
        return certificados;
    }
    
    /**
     * Verifica si un participante puede generar certificado
     */
    public boolean participantePuedeGenerarCertificado(String email, String codigoEvento) throws SQLException {
        // Validaciones básicas
        if (!participanteExiste(email)) {
            throw new SQLException("El participante no está registrado");
        }
        
        if (!eventoExiste(codigoEvento)) {
            throw new SQLException("El evento no existe");
        }
        
        if (!participanteInscritoEnEvento(email, codigoEvento)) {
            throw new SQLException("El participante no está inscrito en este evento");
        }
        
        if (!inscripcionValidada(email, codigoEvento)) {
            throw new SQLException("La inscripción no está validada");
        }
        
        // Verificar asistencia a al menos una actividad
        String sql = "SELECT COUNT(*) AS total_asistencias " +
                     "FROM asistencia a " +
                     "JOIN actividad ac ON a.codigo_actividad = ac.codigo_actividad " +
                     "WHERE a.email_participante = ? AND ac.codigo_evento = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_asistencias") > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Genera un nuevo certificado
     */
    public Certificado generarCertificado(String email, String codigoEvento) throws SQLException {
        // Validaciones previas
        if (!participantePuedeGenerarCertificado(email, codigoEvento)) {
            throw new SQLException("El participante no cumple los requisitos para generar certificado");
        }
        
        if (certificadoExiste(email, codigoEvento)) {
            throw new SQLException("Ya existe un certificado para este participante y evento");
        }
        
        // Obtener datos adicionales
        Certificado certificado = obtenerDatosCertificado(email, codigoEvento);
        
        // Verificar que la fecha no sea null
        if (certificado.getFechaEvento() == null) {
        throw new SQLException("El evento no tiene fecha definida");
        // O alternativamente: certificado.setFechaEvento(new Date());
        }
        
        // Generar nombre de archivo único
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String nombreArchivo = "certificado_" + email + "_" + codigoEvento + "_" + 
                             sdf.format(new Date()) + ".html";
        
        certificado.setRutaArchivo(nombreArchivo);
        
        // Insertar en base de datos
        String sql = "INSERT INTO certificado " +
                     "(email_participante, codigo_evento, ruta_archivo) " +
                     "VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            pstmt.setString(3, nombreArchivo);
            
            pstmt.executeUpdate();
            
            // Obtener el ID generado
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    certificado.setIdCertificado(rs.getInt(1));
                }
            }
        }
        
        return certificado;
    }
    
    /**
     * Obtiene un certificado específico
     */
    public Certificado obtenerCertificado(int idCertificado) throws SQLException {
        String sql = "SELECT c.*, p.nombre_completo, e.titulo_evento " +
                     "FROM certificado c " +
                     "JOIN participante p ON c.email_participante = p.email_participante " +
                     "JOIN evento e ON c.codigo_evento = e.codigo_evento " +
                     "WHERE c.id_certificado = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idCertificado);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Certificado cert = new Certificado();
                    cert.setIdCertificado(rs.getInt("id_certificado"));
                    cert.setEmailParticipante(rs.getString("email_participante"));
                    cert.setCodigoEvento(rs.getString("codigo_evento"));
                    cert.setFechaEmision(rs.getTimestamp("fecha_emision"));
                    cert.setRutaArchivo(rs.getString("ruta_archivo"));
                    cert.setNombreParticipante(rs.getString("nombre_completo"));
                    cert.setTituloEvento(rs.getString("titulo_evento"));
                    return cert;
                }
            }
        }
        
        throw new SQLException("Certificado no encontrado");
    }

    // Métodos auxiliares de validación
    
    private boolean participanteExiste(String email) throws SQLException {
        String sql = "SELECT 1 FROM participante WHERE email_participante = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    private boolean eventoExiste(String codigoEvento) throws SQLException {
        String sql = "SELECT 1 FROM evento WHERE codigo_evento = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, codigoEvento);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    private boolean participanteInscritoEnEvento(String email, String codigoEvento) throws SQLException {
        String sql = "SELECT 1 FROM inscripcion WHERE email_participante = ? AND codigo_evento = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    private boolean inscripcionValidada(String email, String codigoEvento) throws SQLException {
        String sql = "SELECT validada FROM inscripcion WHERE email_participante = ? AND codigo_evento = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getBoolean("validada");
            }
        }
    }
    
    private boolean certificadoExiste(String email, String codigoEvento) throws SQLException {
        String sql = "SELECT 1 FROM certificado WHERE email_participante = ? AND codigo_evento = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    private Certificado obtenerDatosCertificado(String email, String codigoEvento) throws SQLException {
        String sql = "SELECT p.nombre_completo, e.titulo_evento, e.fecha_evento " +
                     "FROM participante p " +
                     "JOIN inscripcion i ON p.email_participante = i.email_participante " +
                     "JOIN evento e ON i.codigo_evento = e.codigo_evento " +
                     "WHERE p.email_participante = ? AND e.codigo_evento = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Certificado cert = new Certificado();
                    cert.setEmailParticipante(email);
                    cert.setCodigoEvento(codigoEvento);
                    cert.setNombreParticipante(rs.getString("nombre_completo"));
                    cert.setTituloEvento(rs.getString("titulo_evento"));
                    
                    // Manejar posible fecha NULL
                    java.sql.Date fechaEvento = rs.getDate("fecha_evento");
                    if (!rs.wasNull()) {
                        cert.setFechaEvento(fechaEvento);
                    } else {
                        cert.setFechaEvento(null); // o establecer una fecha por defecto
                    }
                    return cert;
                }
            }
        }
        
        throw new SQLException("No se pudieron obtener los datos para generar el certificado");
    }    
}
