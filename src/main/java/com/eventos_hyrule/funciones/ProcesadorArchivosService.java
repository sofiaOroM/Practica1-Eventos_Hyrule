/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.funciones;

import com.eventos_hyrule.funciones.Actividad.TipoActividad;
import com.eventos_hyrule.funciones.Evento.TipoEvento;
import com.eventos_hyrule.funciones.Participante.TipoParticipante;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author sofia
 */
public class ProcesadorArchivosService {
   /* private final Connection connection;
    private final Path directorioSalida;
    private final int velocidadMs;
    private StringBuilder logBuilder;
        
    private static final int MAX_LONGITUD_UBICACION = 150;
    private static final int MAX_LONGITUD_NOMBRE = 45;
    private static final int MAX_LONGITUD_TITULO_ACTIVIDAD = 200;
    private static final int MAX_LONGITUD_INSTITUCION = 150;

    public ProcesadorArchivosService(Connection connection, Path directorioSalida, int velocidadMs) {
        this.connection = connection;
        this.directorioSalida = directorioSalida;
        this.velocidadMs = velocidadMs;
        this.logBuilder = new StringBuilder();
    }  
    
    public String procesarArchivo(Path archivoEntrada) throws IOException, SQLException {
        logBuilder.setLength(0); // Limpiar el log
        agregarLog("Iniciando procesamiento del archivo: " + archivoEntrada.getFileName());
        agregarLog("Directorio de salida: " + directorioSalida);
        agregarLog("Velocidad: " + velocidadMs + " ms por instrucción");
        agregarLog("--------------------------------------------------");

        List<String> lineas = Files.readAllLines(archivoEntrada);
        int totalLineas = lineas.size();
        int lineasProcesadas = 0;
        int lineasExitosas = 0;

        try (Statement stmt = connection.createStatement()) {
            for (String linea : lineas) {
                lineasProcesadas++;
                if (linea.trim().isEmpty() || linea.trim().startsWith("//")) {
                    continue; // Saltar líneas vacías o comentarios
                }

                try {
                    procesarLinea(linea, lineasProcesadas);
                    lineasExitosas++;
                } catch (SQLException e) {
                    agregarLog("ERROR en línea " + lineasProcesadas + ": " + e.getMessage());
                } catch (Exception e) {
                    agregarLog("ERROR inesperado en línea " + lineasProcesadas + ": " + e.getMessage());
                }

                // Pausa entre instrucciones si es necesario
                if (velocidadMs > 0) {
                    try {
                        Thread.sleep(velocidadMs);
                    } catch (InterruptedException e) {
                        agregarLog("Procesamiento interrumpido");
                        break;
                    }
                }
            }
        }

        agregarLog("--------------------------------------------------");
        agregarLog("Procesamiento completado.");
        agregarLog("Total de líneas procesadas: " + lineasProcesadas);
        agregarLog("Líneas exitosas: " + lineasExitosas);
        agregarLog("Errores: " + (lineasProcesadas - lineasExitosas));

        return logBuilder.toString();
    }
    
    private void procesarLinea(String linea, int numeroLinea) throws SQLException, IOException {
        agregarLog("Procesando línea " + numeroLinea + ": " + linea);

        if (linea.startsWith("REGISTRO_EVENTO")) {
            procesarRegistroEvento(linea);
        } else if (linea.startsWith("REGISTRO_PARTICIPANTE")) {
            procesarRegistroParticipante(linea);
        } else if (linea.startsWith("INSCRIPCION")) {
            procesarInscripcion(linea);
        } else if (linea.startsWith("PAGO")) {
            procesarPago(linea);
        } else if (linea.startsWith("VALIDAR_INSCRIPCION")) {
            procesarValidacionInscripcion(linea);
        } else if (linea.startsWith("REGISTRO_ACTIVIDAD")) {
            procesarRegistroActividad(linea);
        } else if (linea.startsWith("ASISTENCIA")) {
            procesarAsistencia(linea);
        } else if (linea.startsWith("CERTIFICADO")) {
            procesarCertificado(linea);
        } else if (linea.startsWith("REPORTE_")) {
            procesarReporte(linea);
        } else {
            throw new SQLException("Instrucción no reconocida");
        }
    }
    
    private void procesarRegistroEvento(String linea) throws SQLException {
        String[] params = extraerParametros(linea);
        if (params.length != 7) {
            throw new SQLException("Formato incorrecto. Se esperaban 7 parámetros");
        }

        // Validaciones
        validarTipoEvento(params[2]);
        validarLongitud(params[4], MAX_LONGITUD_UBICACION, "ubicación");
        validarNumeroPositivo(params[5], "cupo máximo");
        validarMonto(params[6], "costo de inscripción");

        // Crear objeto Evento
        Evento evento = new Evento();
        evento.setCodigoEvento(params[0]);
        evento.setFechaEvento(convertirFecha(params[1]));
        evento.setTipoEvento(Evento.TipoEvento.valueOf(params[2]));
        evento.setTituloEvento(params[3]);
        evento.setUbicacion(params[4]);
        evento.setCupoMaximo(Integer.parseInt(params[5]));
        evento.setCostoEvento(Integer.parseInt(params[6]));

        // Guardar en base de datos
        String sql = "INSERT INTO EVENTO (codigo_evento, fecha_evento, tipo_evento, titulo_evento, " +
                    "ubicacion_evento, cupo_maximo_evento, costo_inscripcion) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, evento.getCodigoEvento());
            pstmt.setDate(2, new java.sql.Date(evento.getFechaEvento().getTime()));
            pstmt.setString(3, evento.getTipoEvento().name());
            pstmt.setString(4, evento.getTituloEvento());
            pstmt.setString(5, evento.getUbicacion());
            pstmt.setInt(6, evento.getCupoMaximo());
            pstmt.setDouble(7, evento.getCostoEvento());
            
            pstmt.executeUpdate();
            agregarLog("Evento registrado: " + evento.getCodigoEvento());
        }
    }

    private void procesarRegistroParticipante(String linea) throws SQLException {
        String[] params = extraerParametros(linea);
        if (params.length != 4) {
            throw new SQLException("Formato incorrecto. Se esperaban 4 parámetros");
        }

        // Validaciones
        validarLongitud(params[0], MAX_LONGITUD_NOMBRE, "nombre completo");
        validarTipoParticipante(params[1]);
        validarLongitud(params[2], MAX_LONGITUD_INSTITUCION, "institución");
        validarEmail(params[3]);

        // Verificar si el participante ya existe
        if (participanteExiste(params[3])) {
            throw new SQLException("El participante con email " + params[3] + " ya está registrado");
        }

        // Crear objeto Participante
        Participante participante = new Participante(params[0],                      // nombreCompleto
        params[3],                   
        TipoParticipante.valueOf(params[1]),
        params[2]  
        
        );
        //participante.setNombreCompleto(params[0]);
        //participante.setTipoParticipante(Participante.TipoParticipante.valueOf(params[1]));
        //participante.setInstitucion(params[2]);
        //participante.setEmailParticipante(params[3]);

        // Guardar en base de datos
        String sql = "INSERT INTO PARTICIPANTE (nombre_completo, tipo_participante, institucion, email) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, participante.getNombreCompleto());
            pstmt.setString(2, participante.getTipoParticipante().name());
            pstmt.setString(3, participante.getInstitucion());
            pstmt.setString(4, participante.getEmailParticipante());
            
            pstmt.executeUpdate();
            agregarLog("Participante registrado: " + participante.getEmailParticipante());
        }
    }

    private void procesarInscripcion(String linea) throws SQLException {
        String[] params = extraerParametros(linea);
        if (params.length != 3) {
            throw new SQLException("Formato incorrecto. Se esperaban 3 parámetros");
        }

        // Validaciones
        validarEmail(params[0]);
        validarTipoInscripcion(params[2]);

        // Verificar si el participante existe
        if (!participanteExiste(params[0])) {
            throw new SQLException("El participante con email " + params[0] + " no está registrado");
        }

        // Verificar si el evento existe
        if (!eventoExiste(params[1])) {
            throw new SQLException("El evento con código " + params[1] + " no existe");
        }

        // Verificar cupo disponible
        if (!hayCupoDisponible(params[1])) {
            throw new SQLException("No hay cupo disponible para el evento " + params[1]);
        }

        // Verificar inscripción duplicada
        if (inscripcionExiste(params[0], params[1])) {
            throw new SQLException("El participante ya está inscrito en este evento");
        }

        // Crear objeto ParticipanteEvento (Inscripción)
        ParticipanteEvento inscripcion = new ParticipanteEvento();
        inscripcion.setEmail(params[0]);
        inscripcion.setCodigoEvento(params[1]);
        inscripcion.setTipoInscripcion(ParticipanteEvento.TipoInscripcion.valueOf(params[2]));
        inscripcion.setValidada(false);

        // Guardar en base de datos
        String sql = "INSERT INTO INSCRIPCION (email_participante, codigo_evento, tipo_inscripcion, validada) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, inscripcion.getEmailParticipante());
            pstmt.setString(2, inscripcion.getCodigoEvento());
            pstmt.setString(3, inscripcion.getTipoInscripcion().name());
            pstmt.setBoolean(4, inscripcion.isValidada());
            
            pstmt.executeUpdate();
            agregarLog("Inscripción registrada para: " + inscripcion.getEmailParticipante() + 
                       " en evento " + inscripcion.getCodigoEvento());
        }
    }

    private void procesarPago(String linea) throws SQLException {
        String[] params = extraerParametros(linea);
        if (params.length != 4) {
            throw new SQLException("Formato incorrecto. Se esperaban 4 parámetros");
        }

        // Validaciones
        validarEmail(params[0]);
        validarMetodoPago(params[2]);
        validarMonto(params[3], "monto");

        // Verificar si existe la inscripción
        if (!inscripcionExiste(params[0], params[1])) {
            throw new SQLException("No existe inscripción para validar este pago");
        }

        // Verificar si ya existe un pago para esta inscripción
        if (pagoExiste(params[0], params[1])) {
            throw new SQLException("Ya existe un pago registrado para esta inscripción");
        }

        // Crear objeto Pago (asumiendo que existe la clase modelo)
        Pago pago = new Pago();
        pago.setEmailParticipante(params[0]);
        pago.setCodigoEvento(params[1]);
        pago.setMetodoPago(params[2]);
        pago.setMonto(new BigDecimal(params[3]));
        pago.setFecha(new Date());

        // Guardar en base de datos
        String sql = "INSERT INTO PAGO (email_participante, codigo_evento, metodo_pago, monto, fecha) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, pago.getEmailParticipante());
            pstmt.setString(2, pago.getCodigoEvento());
            pstmt.setString(3, pago.getMetodoPago());
            pstmt.setBigDecimal(4, pago.getMonto());
            pstmt.setDate(5, new java.sql.Date(pago.getFecha().getTime()));
            
            pstmt.executeUpdate();
            agregarLog("Pago registrado para: " + pago.getEmailParticipante() + 
                      " en evento " + pago.getCodigoEvento());
        }
    }

    private void procesarValidacionInscripcion(String linea) throws SQLException {
        String[] params = extraerParametros(linea);
        if (params.length != 2) {
            throw new SQLException("Formato incorrecto. Se esperaban 2 parámetros");
        }

        validarEmail(params[0]);

        // Verificar si existe pago
        if (!pagoExiste(params[0], params[1])) {
            throw new SQLException("No se puede validar la inscripción sin pago registrado");
        }

        // Verificar si ya está validada
        if (inscripcionValidada(params[0], params[1])) {
            throw new SQLException("La inscripción ya está validada");
        }

        String sql = "UPDATE INSCRIPCION SET validada = TRUE " +
                    "WHERE email_participante = ? AND codigo_evento = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, params[0]);
            pstmt.setString(2, params[1]);
            
            int affected = pstmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("No se encontró la inscripción para validar");
            }
            agregarLog("Inscripción validada para: " + params[0] + " en evento " + params[1]);
        }
    }
    
    private void procesarRegistroActividad(String linea) throws SQLException {
        String[] params = extraerParametros(linea);
        if (params.length != 8) {
            throw new SQLException("Formato incorrecto. Se esperaban 8 parámetros");
        }

        // Validaciones
        validarTipoActividad(params[2]);
        validarLongitud(params[3], MAX_LONGITUD_TITULO_ACTIVIDAD, "título de actividad");
        validarEmail(params[4]);
        validarHora(params[5], "hora de inicio");
        validarHora(params[6], "hora de fin");
        validarNumeroPositivo(params[7], "cupo máximo");

        // Verificar si el evento existe
        if (!eventoExiste(params[1])) {
            throw new SQLException("El evento con código " + params[1] + " no existe");
        }

        // Verificar si el responsable existe y tiene rol adecuado
        if (!responsableValido(params[4], params[1])) {
            throw new SQLException("El responsable no está registrado o no tiene un rol válido");
        }

        // Crear objeto Actividad
        Actividad actividad = new Actividad();
        actividad.setCodigoActividad(params[0]);
        actividad.setCodigoEvento(params[1]);
        actividad.setTipoActividad(Actividad.TipoActividad.valueOf(params[2]));
        actividad.setTituloActividad(params[3]);
        actividad.setEmailEncargado(params[4]);
        actividad.setHoraInicio(params[5]);
        actividad.setHoraFin(params[6]);
        actividad.setCupoMaximo(Integer.parseInt(params[7]));

        // Guardar en base de datos
        String sql = "INSERT INTO ACTIVIDAD (codigo_actividad, codigo_evento, tipo_actividad, " +
                    "titulo_actividad, responsable, hora_inicio, hora_fin, cupo_maximo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, actividad.getCodigoActividad());
            pstmt.setString(2, actividad.getCodigoEvento());
            pstmt.setString(3, actividad.getTipoActividad().name());
            pstmt.setString(4, actividad.getTituloActividad());
            pstmt.setString(5, actividad.getResponsable());
            pstmt.setString(6, actividad.getHoraInicio());
            pstmt.setString(7, actividad.getHoraFin());
            pstmt.setInt(8, actividad.getCupoMaximo());
            
            pstmt.executeUpdate();
            agregarLog("Actividad registrada: " + actividad.getCodigoActividad() + 
                      " para evento " + actividad.getCodigoEvento());
        }
    }

    private void procesarAsistencia(String linea) throws SQLException {
        String[] params = extraerParametros(linea);
        if (params.length != 2) {
            throw new SQLException("Formato incorrecto. Se esperaban 2 parámetros");
        }

        validarEmail(params[0]);

        // Verificar si el participante está inscrito en el evento de la actividad
        if (!participanteInscritoEnActividad(params[0], params[1])) {
            throw new SQLException("El participante no está inscrito en el evento de esta actividad");
        }

        // Verificar si la asistencia ya está registrada
        if (asistenciaRegistrada(params[0], params[1])) {
            throw new SQLException("La asistencia ya está registrada");
        }

        // Crear objeto Asistencia
        Asistencia asistencia = new Asistencia();
        asistencia.setEmailParticipante(params[0]);
        asistencia.setCodigoActividad(params[1]);
        asistencia.setFechaAsistencia(new Date());

        // Guardar en base de datos
        String sql = "INSERT INTO ASISTENCIA (email_participante, codigo_actividad, fecha_hora) " +
                    "VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, asistencia.getEmailParticipante());
            pstmt.setString(2, asistencia.getCodigoActividad());
            pstmt.setTimestamp(3, new java.sql.Timestamp(asistencia.getFechaAsistencia().getTime()));
            
            pstmt.executeUpdate();
            agregarLog("Asistencia registrada para: " + asistencia.getEmailParticipante() + 
                       " en actividad " + asistencia.getCodigoActividad());
        }
    }

    private void procesarCertificado(String linea) throws SQLException, IOException {
        String[] params = extraerParametros(linea);
        if (params.length != 2) {
            throw new SQLException("Formato incorrecto. Se esperaban 2 parámetros");
        }

        validarEmail(params[0]);

        // Verificar si el participante asistió al menos a una actividad del evento
        if (!participanteAsistioAEvento(params[0], params[1])) {
            throw new SQLException("El participante no ha asistido a ninguna actividad del evento");
        }

        // Obtener datos para el certificado
        Certificado certificado = generarDatosCertificado(params[0], params[1]);

        // Generar HTML del certificado
        String html = generarHtmlCertificado(certificado);
        
        // Guardar archivo
        String nombreArchivo = "certificado_" + params[0] + "_" + params[1] + ".html";
        Path archivoCertificado = directorioSalida.resolve(nombreArchivo);
        Files.write(archivoCertificado, html.getBytes(StandardCharsets.UTF_8));
        
        agregarLog("Certificado generado: " + nombreArchivo);
    }

    private Certificado generarDatosCertificado(String email, String codigoEvento) throws SQLException {
        String sql = "SELECT p.nombre_completo, e.titulo_evento, e.fecha_evento " +
                    "FROM PARTICIPANTE p " +
                    "JOIN INSCRIPCION i ON p.email = i.email_participante " +
                    "JOIN EVENTO e ON i.codigo_evento = e.codigo_evento " +
                    "WHERE p.email = ? AND e.codigo_evento = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Certificado certificado = new Certificado();
                    certificado.setNombreParticipante(rs.getString("nombre_completo"));
                    certificado.setTituloEvento(rs.getString("titulo_evento"));
                    certificado.setFechaEvento(rs.getDate("fecha_evento"));
                    certificado.setEmailParticipante(email);
                    certificado.setCodigoEvento(codigoEvento);
                    certificado.setFechaGeneracion(new Date());
                    return certificado;
                }
                throw new SQLException("No se encontraron datos para generar el certificado");
            }
        }
    }

    private String generarHtmlCertificado(Certificado certificado) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<title>Certificado de Participación</title>" +
               "<style>body { font-family: Arial; margin: 50px; }</style>" +
               "</head>" +
               "<body>" +
               "<h1 style='text-align: center;'>CERTIFICADO DE PARTICIPACIÓN</h1>" +
               "<p style='text-align: center;'>Se certifica que</p>" +
               "<h2 style='text-align: center;'>" + certificado.getNombreParticipante() + "</h2>" +
               "<p style='text-align: center;'>participó en el evento</p>" +
               "<h3 style='text-align: center;'>" + certificado.getTituloEvento() + "</h3>" +
               "<p style='text-align: center;'>realizado el " + sdf.format(certificado.getFechaEvento()) + "</p>" +
               "<p style='text-align: right; margin-top: 50px;'>Generado el: " + 
               sdf.format(certificado.getFechaGeneracion()) + "</p>" +
               "</body>" +
               "</html>";
    } 
    
    private void agregarLog(String mensaje) {
        logBuilder.append(mensaje).append("\n");
    }
    
    private String[] extraerParametros(String linea) {
        // Expresión regular para extraer contenido entre comillas
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(linea);
        
        // Contar número de parámetros
        int count = 0;
        while (matcher.find()) count++;
        
        if (count == 0) {
            return new String[0];
        }
        
        // Extraer parámetros
        String[] params = new String[count];
        matcher = pattern.matcher(linea);
        int i = 0;
        while (matcher.find()) {
            params[i++] = matcher.group(1);
        }
        
        return params;
    }
        
    private void validarEmail(String email) throws SQLException {
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new SQLException("Formato de email inválido: " + email);
        }
    }

    private void validarTipoEvento(String tipo) throws SQLException {
        try {
            TipoEvento.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Tipo de evento inválido: " + tipo);
        }
    }

    private void validarTipoParticipante(String tipo) throws SQLException {
        try {
            TipoParticipante.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Tipo de participante inválido: " + tipo);
        }
    }

    private void validarTipoInscripcion(String tipo) throws SQLException {
        try {
            TipoInscripcion.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Tipo de inscripción inválido: " + tipo);
        }
    }

    private void validarMetodoPago(String metodo) throws SQLException {
        try {
            MetodoPago.valueOf(metodo);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Método de pago inválido: " + metodo);
        }
    }

    private void validarTipoActividad(String tipo) throws SQLException {
        try {
            TipoActividad.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Tipo de actividad inválido: " + tipo);
        }
    }

    private void validarLongitud(String valor, int maxLongitud, String campo) throws SQLException {
        if (valor.length() > maxLongitud) {
            throw new SQLException(campo + " excede la longitud máxima de " + maxLongitud + " caracteres");
        }
    }

    private void validarNumeroPositivo(String valor, String campo) throws SQLException {
        try {
            int num = Integer.parseInt(valor);
            if (num <= 0) {
                throw new SQLException(campo + " debe ser un número positivo");
            }
        } catch (NumberFormatException e) {
            throw new SQLException(campo + " debe ser un número válido");
        }
    }

    private void validarMonto(String valor, String campo) throws SQLException {
        try {
            new BigDecimal(valor);
        } catch (NumberFormatException e) {
            throw new SQLException(campo + " debe ser un valor numérico válido");
        }
    }

    private void validarHora(String hora, String campo) throws SQLException {
        if (!hora.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            throw new SQLException("Formato de " + campo + " inválido (hh:mm)");
        }
    }

    // Métodos de verificación en base de datos
    private boolean participanteExiste(String email) throws SQLException {
        String sql = "SELECT 1 FROM PARTICIPANTE WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean eventoExiste(String codigoEvento) throws SQLException {
        String sql = "SELECT 1 FROM EVENTO WHERE codigo_evento = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, codigoEvento);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean hayCupoDisponible(String codigoEvento) throws SQLException {
        String sql = "SELECT e.cupo_maximo_evento, COUNT(i.email_participante) as inscritos " +
                     "FROM EVENTO e LEFT JOIN INSCRIPCION i ON e.codigo_evento = i.codigo_evento " +
                     "WHERE e.codigo_evento = ? GROUP BY e.cupo_maximo_evento";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, codigoEvento);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int cupoMaximo = rs.getInt("cupo_maximo_evento");
                    int inscritos = rs.getInt("inscritos");
                    return inscritos < cupoMaximo;
                }
                return false;
            }
        }
    }

    private boolean inscripcionExiste(String email, String codigoEvento) throws SQLException {
        String sql = "SELECT 1 FROM INSCRIPCION WHERE email_participante = ? AND codigo_evento = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean pagoExiste(String email, String codigoEvento) throws SQLException {
        String sql = "SELECT 1 FROM PAGO WHERE email_participante = ? AND codigo_evento = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean inscripcionValidada(String email, String codigoEvento) throws SQLException {
        String sql = "SELECT validada FROM INSCRIPCION WHERE email_participante = ? AND codigo_evento = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getBoolean("validada");
            }
        }
    }

    private boolean responsableValido(String email, String codigoEvento) throws SQLException {
        String sql = "SELECT 1 FROM INSCRIPCION i " +
                     "WHERE i.email_participante = ? AND i.codigo_evento = ? " +
                     "AND i.tipo_inscripcion IN ('CONFERENCISTA', 'TALLERISTA')";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean participanteInscritoEnActividad(String email, String codigoActividad) throws SQLException {
        String sql = "SELECT 1 FROM INSCRIPCION i " +
                     "JOIN ACTIVIDAD a ON i.codigo_evento = a.codigo_evento " +
                     "WHERE i.email_participante = ? AND a.codigo_actividad = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoActividad);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean asistenciaRegistrada(String email, String codigoActividad) throws SQLException {
        String sql = "SELECT 1 FROM ASISTENCIA WHERE email_participante = ? AND codigo_actividad = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoActividad);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean participanteAsistioAEvento(String email, String codigoEvento) throws SQLException {
        String sql = "SELECT 1 FROM ASISTENCIA a " +
                     "JOIN ACTIVIDAD ac ON a.codigo_actividad = ac.codigo_actividad " +
                     "WHERE a.email_participante = ? AND ac.codigo_evento = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    private Date convertirFecha(String fechaStr) throws SQLException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date fechaUtil = sdf.parse(fechaStr);
            return new Date(fechaUtil.getTime());
        } catch (ParseException e) {
            throw new SQLException("Formato de fecha inválido: " + fechaStr);
        }
    } */
}
