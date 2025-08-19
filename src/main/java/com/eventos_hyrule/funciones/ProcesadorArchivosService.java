/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.funciones;

import com.eventos_hyrule.conexionDB;
import com.eventos_hyrule.funciones.Actividad.TipoActividad;
import com.eventos_hyrule.funciones.Evento.TipoEvento;
import com.eventos_hyrule.funciones.Participante.TipoParticipante;
import com.eventos_hyrule.funciones.Inscripcion.TipoInscripcion;
import com.eventos_hyrule.funciones.Pago.MetodoPago;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
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
    private final Connection connection;
    private final Path directorioSalida;
    private final int velocidadMs;
    private StringBuilder logBuilder;
        
    private static final int MAX_LONGITUD_UBICACION = 150;
    private static final int MAX_LONGITUD_NOMBRE = 45;
    private static final int MAX_LONGITUD_TITULO_ACTIVIDAD = 200;
    private static final int MAX_LONGITUD_INSTITUCION = 150;

    public ProcesadorArchivosService(Connection connection, Path directorioSalida, int velocidadMs) {
        conexionDB conexion = new conexionDB();
        conexion.Connect();
        this.connection = conexion.getConnection();        this.directorioSalida = directorioSalida;
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
                    agregarLog("   ERROR en línea " + lineasProcesadas + ": " + e.getMessage());
                } catch (Exception e) {
                    agregarLog("   ERROR inesperado en línea " + lineasProcesadas + ": " + e.getMessage());
                }

                // Pausa entre instrucciones si es necesario
                if (velocidadMs > 0) {
                    try {
                        Thread.sleep(velocidadMs);
                    } catch (InterruptedException e) {
                        agregarLog("----- Procesamiento interrumpido -----");
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

        if (linea.startsWith("REGISTRO_EVENTO(")) {
            procesarRegistroEvento(linea);
        } else if (linea.startsWith("REGISTRO_PARTICIPANTE(")) {
            procesarRegistroParticipante(linea);
        } else if (linea.startsWith("INSCRIPCION(")) {
            procesarInscripcion(linea);
        } else if (linea.startsWith("PAGO(")) {
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

        Date fechaEvento = convertirFecha(params[1]);

        // Crear objeto Evento
        Evento evento = new Evento(
            params[0], // codigo
            fechaEvento,
            Evento.TipoEvento.valueOf(params[2]), // tipo
            params[3], // titulo
            params[4], // ubicacion
            Integer.parseInt(params[5]), 
                (int) Double.parseDouble(params[6])
        );

        // Guardar en base de datos
        String sql = "INSERT INTO EVENTO (codigo_evento, fecha_evento, tipo_evento, titulo_evento, " +
                    "ubicacion_evento, cupo_maximo_evento, costo_evento) " +
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
            agregarLog("   Evento registrado: " + evento.getCodigoEvento());
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
        Participante participante = new Participante(
            params[3],
            params[0],                      // nombreCompleto        
            Participante.TipoParticipante.valueOf(params[1]), // tipo
            params[2]
        );

        // Guardar en base de datos
        String sql = "INSERT INTO PARTICIPANTE (email_participante, nombre_completo, tipo_participante, institucion) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, participante.getEmailParticipante());
            pstmt.setString(2, participante.getNombreCompleto());
            pstmt.setString(3, participante.getTipoParticipante().name());
            pstmt.setString(4, participante.getInstitucion());
            
            pstmt.executeUpdate();
            agregarLog("   Participante registrado: " + participante.getEmailParticipante());
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
        Inscripcion inscripcion = new Inscripcion(
            params[0], // email
            params[1], // codigo evento
            Inscripcion.TipoInscripcion.valueOf(params[2]), // tipo
            false // validada
        );
        // Guardar en base de datos
        String sql = "INSERT INTO INSCRIPCION (email_participante, codigo_evento, tipo_inscripcion, validada) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, inscripcion.getEmailParticipante());
            pstmt.setString(2, inscripcion.getCodigoEvento());
            pstmt.setString(3, inscripcion.getTipoInscripcion().name());
            pstmt.setBoolean(4, inscripcion.getValidada());
            
            pstmt.executeUpdate();
            agregarLog("   Inscripción registrada para: " + inscripcion.getEmailParticipante() + 
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
        Pago pago = new Pago(
            params[0], // email
            params[1], // codigo evento
            params[2], // metodo pago
            new BigDecimal(params[3]), // monto
            new Date(System.currentTimeMillis()) // fecha actual
        );

        // Guardar en base de datos
        String sql = "INSERT INTO PAGO (email_participante, codigo_evento, metodo_pago, monto, fecha_pago) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, pago.getEmailParticipante());
            pstmt.setString(2, pago.getCodigoEvento());
            pstmt.setString(3, pago.getMetodoPago());
            pstmt.setBigDecimal(4, pago.getMonto());
            pstmt.setTimestamp(5, new java.sql.Timestamp(pago.getFecha().getTime()));
            
            pstmt.executeUpdate();
            agregarLog("   Pago registrado para: " + pago.getEmailParticipante() + 
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
            agregarLog("   Inscripción validada para: " + params[0] + " en evento " + params[1]);
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
        
        Time horaInicio = convertirHora(params[5]);
        Time horaFin = convertirHora(params[6]);
        
        // Crear objeto Actividad
        Actividad actividad = new Actividad(
            params[0], // codigo
            params[1], // codigo evento
            Actividad.TipoActividad.valueOf(params[2]), // tipo
            params[3], // titulo
            params[4], // responsable
            horaInicio,
            horaFin,
            Integer.parseInt(params[7]) // cupo
        );
                
        // Guardar en base de datos
        String sql = "INSERT INTO ACTIVIDAD (codigo_actividad, codigo_evento, tipo_actividad, " +
                    "titulo_actividad, email_encargado, hora_inicio, hora_fin, cupo_maximo_actividad) " +
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
            
            pstmt.executeUpdate();
            agregarLog("   Actividad registrada: " + actividad.getCodigoActividad() + 
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
        Asistencia asistencia = new Asistencia(
            params[0], // email
            params[1], // codigo actividad
            new Timestamp(System.currentTimeMillis()) // fecha actual
        );

        // Guardar en base de datos
        String sql = "INSERT INTO ASISTENCIA (email_participante, codigo_actividad, fecha_asistencia) " +
                    "VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, asistencia.getEmailParticipante());
            pstmt.setString(2, asistencia.getCodigoActividad());
            pstmt.setTimestamp(3, new java.sql.Timestamp(asistencia.getFechaAsistencia().getTime()));
            
            pstmt.executeUpdate();
            agregarLog("   Asistencia registrada para: " + asistencia.getEmailParticipante() + 
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
        Certificado certificado = generarDatosCertificado(
            params[0], 
            params[1],
            new Timestamp(System.currentTimeMillis())

        );

        // Generar HTML del certificado
        String html = generarHtmlCertificado(certificado);
        
        // Guardar archivo
        String nombreArchivo = "certificado_" + params[0] + "_" + params[1] + ".html";
        Path archivoCertificado = directorioSalida.resolve(nombreArchivo);
        Files.write(archivoCertificado, html.getBytes(StandardCharsets.UTF_8));
        
        agregarLog("   Certificado generado: " + nombreArchivo);
    }

    private Certificado generarDatosCertificado(String email, String codigoEvento, Timestamp fechaEmision) throws SQLException {
        String sql = "SELECT p.nombre_completo, e.titulo_evento, e.fecha_evento " +
                    "FROM PARTICIPANTE p " +
                    "JOIN INSCRIPCION i ON p.email_participante = i.email_participante " +
                    "JOIN EVENTO e ON i.codigo_evento = e.codigo_evento " +
                    "WHERE p.email_participante = ? AND e.codigo_evento = ?";
        
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
                    certificado.setFechaEmision(fechaEmision);
                    return certificado;
                }
                throw new SQLException("No se encontraron datos para generar el certificado");
            }
        }
    }
    
    
    private void procesarReporte(String linea) throws SQLException, IOException {
        if (linea.startsWith("REPORTE_EVENTOS")) {
            procesarReporteEventos(linea);
        } else if (linea.startsWith("REPORTE_PARTICIPANTES")) {
            procesarReporteParticipantes(linea);
        } else if (linea.startsWith("REPORTE_ACTIVIDADES")) {
            procesarReporteActividades(linea);
        } else {
            throw new SQLException("Tipo de reporte no reconocido: " + linea);
        }
    }

    private String generarHtmlCertificad(Certificado certificado) {
        
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
               sdf.format(certificado.getFechaEmision()) + "</p>" +
               "</body>" +
               "</html>";
    } 
    
    private String generarHtmlCertificado(Certificado certificado) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "<!DOCTYPE html>" +
               "<html lang='es'>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
               "<title>Certificado de Participación</title>" +
               "<style>" +
               "body { margin: 0; padding: 0; font-family: 'Montserrat', Arial, sans-serif; background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%); display: flex; justify-content: center; align-items: center; min-height: 100vh; }" +
               ".certificado { width: 800px; background: white; border-radius: 15px; box-shadow: 0 20px 40px rgba(0,0,0,0.15); position: relative; overflow: hidden; border: 1px solid #e0e0e0; padding: 40px; }" +
               ".certificado-border { position: absolute; width: 96%; height: 94%; border: 2px dashed #3a7bd5; border-radius: 10px; top: 2%; left: 2%; z-index: 1; opacity: 0.3; }" +
               ".watermark { position: absolute; opacity: 0.05; font-size: 180px; font-weight: 900; color: #3a7bd5; top: 50%; left: 50%; transform: translate(-50%, -50%); z-index: 0; user-select: none; }" +
               ".titulo { color: #3a7bd5; font-size: 36px; font-weight: 700; margin-bottom: 10px; letter-spacing: 2px; text-align: center; }" +
               ".subtitulo { color: #666; font-size: 18px; text-align: center; margin-bottom: 30px; }" +
               ".nombre { font-size: 32px; font-weight: 700; color: #222; text-align: center; margin: 30px 0; text-transform: uppercase; letter-spacing: 1px; }" +
               ".email { font-size: 12px; color: #555; text-align: center; margin-bottom: 20px; }" +
               ".evento { font-size: 28px; font-weight: 600; color: #333; text-align: center; margin: 20px 0; padding: 15px; background: linear-gradient(90deg, rgba(58,123,213,0.1) 0%, rgba(58,123,213,0.05) 100%); border-left: 4px solid #3a7bd5; }" +
               ".fecha-evento { font-size: 16px; color: #555; text-align: center; margin-bottom: 30px; }" +
               ".detalles { display: flex; justify-content: space-around; margin: 30px 0; }" +
               ".detalle { text-align: center; }" +
               ".detalle-label { font-size: 14px; color: #777; margin-bottom: 5px; }" +
               ".detalle-valor { font-size: 16px; font-weight: 600; color: #444; }" +
               ".firma { position: absolute; right: 40px; bottom: 40px; text-align: center; }" +
               ".firma-nombre { font-weight: 600; margin-top: 10px; color: #333; }" +
               ".firma-cargo { font-size: 12px; color: #777; margin-top: 5px; }" +
               ".codigo { position: absolute; bottom: 15px; left: 30px; font-size: 12px; color: #999; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='certificado'>" +
               "<div class='certificado-border'></div>" +
               "<div class='watermark'>CERTIFICADO</div>" +
               "<div class='titulo'>CERTIFICADO DE PARTICIPACIÓN</div>" +
               "<div class='subtitulo'>Reino de Hyrule tiene el honor de otorgar a: </div>" +
               "<div class='nombre'>" + certificado.getNombreParticipante() + "</div>" +
               "<div class='email'>Correo electrónico: <strong>" + certificado.getEmailParticipante() + "</strong></div>" +
               "<div class='subtitulo'>Por su notable participacion en el evento " + certificado.getCodigoEvento() + " titulado: </div>" +
               "<div class='evento'>" + certificado.getTituloEvento() + "</div>" +
               "<div class='subtitulo'>realizado el " + sdf.format(certificado.getFechaEvento()) + "</div>" +
               "<div class='detalles'>" +
               "<div class='detalle'>" +
               "<div class='detalle-label'>Fecha de emisión</div>" +
               "<div class='detalle-valor'>" + certificado.getFechaEmisionFormateada() + "</div>" +
               "</div>" +
               "<div class='detalle'>" +
               "<div class='detalle-label'>Código de verificación</div>" +
               "<div class='detalle-valor'> CE-" + certificado.getIdCertificado() + "</div>" +
               "</div>" +
               "</div>" +
               "<div class='codigo'>ID: " + certificado.getIdCertificado() + "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
    
    private void agregarLog(String mensaje) {
        logBuilder.append(mensaje).append("\n");
    }
    
    private String[] extraerParametros(String linea) {
        // Quitar el nombre del comando y paréntesis
        int inicio = linea.indexOf('(');
        int fin = linea.lastIndexOf(')');
        if (inicio < 0 || fin < 0 || fin <= inicio) return new String[0];

        String contenido = linea.substring(inicio + 1, fin).trim();

        // Dividir por comas, conservando valores vacíos
        String[] params = contenido.split(",", -1); // -1 -> conserva vacíos al final

        // Limpiar espacios y comillas
        for (int i = 0; i < params.length; i++) {
            params[i] = params[i].trim().replaceAll("^\"|\"$", "");
        }

        return params;
    }   
  
    private Date convertirFecha(String fechaStr) throws SQLException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return new Date(sdf.parse(fechaStr).getTime());
        } catch (ParseException e) {
            throw new SQLException("Formato de fecha inválido: " + fechaStr);
        }
    }
    
    private Time convertirHora(String horaStr) throws SQLException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return new Time(sdf.parse(horaStr).getTime());
        } catch (ParseException e) {
            throw new SQLException("Formato de hora inválido: " + horaStr);
        }
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
        String sql = "SELECT 1 FROM PARTICIPANTE WHERE email_participante = ?";
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
    /**
    * Genera reporte de eventos con filtros
    */
    private void procesarReporteEventos(String linea) throws SQLException, IOException {
        String[] params = extraerParametros(linea);

        // Validar parámetros
        if (params.length != 5) {
            throw new SQLException("Formato incorrecto. Se esperaban 5 parámetros para REPORTE_EVENTOS");
        }

        String tipoEvento = params[0].isEmpty() ? null : params[0];
        String fechaInicio = params[1].isEmpty() ? null : params[1];
        String fechaFin = params[2].isEmpty() ? null : params[2];
        String cupoMin = params[3].isEmpty() ? null : params[3];
        String cupoMax = params[4].isEmpty() ? null : params[4];

        // Construir consulta SQL con filtros
        StringBuilder sql = new StringBuilder(
            "SELECT e.*, COUNT(i.email_participante) as total_inscritos, " +
            "SUM(CASE WHEN i.validada THEN 1 ELSE 0 END) as validados " +
            "FROM evento e LEFT JOIN inscripcion i ON e.codigo_evento = i.codigo_evento " +
            "WHERE 1=1"
        );

        if (tipoEvento != null) {
            sql.append(" AND e.tipo_evento = ?");
        }
        if (fechaInicio != null && fechaFin != null) {
            sql.append(" AND e.fecha_evento BETWEEN STR_TO_DATE(?, '%d/%m/%Y') AND STR_TO_DATE(?, '%d/%m/%Y')");
        }
        if (cupoMin != null && cupoMax != null) {
            sql.append(" AND e.cupo_maximo_evento BETWEEN ? AND ?");
        }

        sql.append(" GROUP BY e.codigo_evento");

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (tipoEvento != null) {
                pstmt.setString(paramIndex++, tipoEvento);
            }
            if (fechaInicio != null && fechaFin != null) {
                pstmt.setString(paramIndex++, fechaInicio);
                pstmt.setString(paramIndex++, fechaFin);
            }
            if (cupoMin != null && cupoMax != null) {
                pstmt.setInt(paramIndex++, Integer.parseInt(cupoMin));
                pstmt.setInt(paramIndex++, Integer.parseInt(cupoMax));
            }

            ResultSet rs = pstmt.executeQuery();

            // Generar HTML del reporte
            StringBuilder html = new StringBuilder();
            html.append(encabezadoHTML("Reporte de Eventos"));
            html.append("<table border='1'><tr>")
               .append("<th>Código</th><th>Título</th><th>Tipo</th><th>Fecha</th>")
               .append("<th>Ubicación</th><th>Cupo</th><th>Inscritos</th><th>Validados</th>")
               .append("</tr>");

            while (rs.next()) {
                html.append("<tr>")
                   .append("<td>").append(rs.getString("codigo_evento")).append("</td>")
                   .append("<td>").append(rs.getString("titulo_evento")).append("</td>")
                   .append("<td>").append(rs.getString("tipo_evento")).append("</td>")
                   .append("<td>").append(rs.getDate("fecha_evento")).append("</td>")
                   .append("<td>").append(rs.getString("ubicacion_evento")).append("</td>")
                   .append("<td>").append(rs.getInt("cupo_maximo_evento")).append("</td>")
                   .append("<td>").append(rs.getInt("total_inscritos")).append("</td>")
                   .append("<td>").append(rs.getInt("validados")).append("</td>")
                   .append("</tr>");
            }

            html.append("</table>");
            html.append(pieHTML());

            // Guardar archivo
            guardarArchivoReporte(html.toString(), "reporte_eventos");
        }
    }

    /**
    * Genera reporte de participantes con filtros
    */
    private void procesarReporteParticipantes(String linea) throws SQLException, IOException {
        String[] params = extraerParametros(linea);

        if (params.length != 3) {
            throw new SQLException("Formato incorrecto. Se esperaban 3 parámetros para REPORTE_PARTICIPANTES");
        }

        String codigoEvento = params[0];
        String tipoParticipante = params[1].isEmpty() ? null : params[1];
        String institucion = params[2].isEmpty() ? null : params[2];

        StringBuilder sql = new StringBuilder(
            "SELECT p.*, i.validada FROM participante p " +
            "JOIN inscripcion i ON p.email_participante = i.email_participante " +
            "WHERE i.codigo_evento = ?"
        );

        if (tipoParticipante != null) {
            sql.append(" AND p.tipo_participante = ?");
        }
        if (institucion != null) {
            sql.append(" AND p.institucion LIKE ?");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            pstmt.setString(1, codigoEvento);
            int paramIndex = 2;
            if (tipoParticipante != null) {
                pstmt.setString(paramIndex++, tipoParticipante);
            }
            if (institucion != null) {
                pstmt.setString(paramIndex++, "%" + institucion + "%");
            }

            ResultSet rs = pstmt.executeQuery();

            StringBuilder html = new StringBuilder();
            html.append(encabezadoHTML("Reporte de Participantes - Evento " + codigoEvento));
            html.append("<table border='1'><tr>")
               .append("<th>Email</th><th>Nombre</th><th>Tipo</th>")
               .append("<th>Institución</th><th>Inscripción Validada</th>")
               .append("</tr>");

            while (rs.next()) {
                html.append("<tr>")
                   .append("<td>").append(rs.getString("email_participante")).append("</td>")
                   .append("<td>").append(rs.getString("nombre_completo")).append("</td>")
                   .append("<td>").append(rs.getString("tipo_participante")).append("</td>")
                   .append("<td>").append(rs.getString("institucion")).append("</td>")
                   .append("<td>").append(rs.getBoolean("validada") ? "Sí" : "No").append("</td>")
                   .append("</tr>");
            }

            html.append("</table>");
            html.append(pieHTML());

            guardarArchivoReporte(html.toString(), "reporte_participantes_" + codigoEvento);
        }
    }

    /**
    * Genera reporte de actividades con filtros
    */
    private void procesarReporteActividades(String linea) throws SQLException, IOException {
        String[] params = extraerParametros(linea);

        if (params.length != 3) {
            throw new SQLException("Formato incorrecto. Se esperaban 3 parámetros para REPORTE_ACTIVIDADES");
        }

        String codigoEvento = params[0];
        String tipoActividad = params[1].isEmpty() ? null : params[1];
        String responsable = params[2].isEmpty() ? null : params[2];

        StringBuilder sql = new StringBuilder(
            "SELECT a.*, p.nombre_completo as nombre_responsable, " +
            "COUNT(asist.email_participante) as asistentes " +
            "FROM actividad a " +
            "LEFT JOIN participante p ON a.email_encargado = p.email_participante " +
            "LEFT JOIN asistencia asist ON a.codigo_actividad = asist.codigo_actividad " +
            "WHERE a.codigo_evento = ?"
        );

        if (tipoActividad != null) {
            sql.append(" AND a.tipo_actividad = ?");
        }
        if (responsable != null) {
            sql.append(" AND a.email_encargado = ?");
        }

        sql.append(" GROUP BY a.codigo_actividad");

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            pstmt.setString(1, codigoEvento);
            int paramIndex = 2;
            if (tipoActividad != null) {
                pstmt.setString(paramIndex++, tipoActividad);
            }
            if (responsable != null) {
                pstmt.setString(paramIndex++, responsable);
            }

            ResultSet rs = pstmt.executeQuery();

            StringBuilder html = new StringBuilder();
            html.append(encabezadoHTML("Reporte de Actividades - Evento " + codigoEvento));
            html.append("<table border='1'><tr>")
               .append("<th>Código</th><th>Tipo</th><th>Título</th><th>Responsable</th>")
               .append("<th>Hora Inicio</th><th>Hora Fin</th><th>Cupo</th><th>Asistentes</th>")
               .append("</tr>");

            while (rs.next()) {
                html.append("<tr>")
                   .append("<td>").append(rs.getString("codigo_actividad")).append("</td>")
                   .append("<td>").append(rs.getString("tipo_actividad")).append("</td>")
                   .append("<td>").append(rs.getString("titulo_actividad")).append("</td>")
                   .append("<td>").append(rs.getString("nombre_responsable")).append("</td>")
                   .append("<td>").append(rs.getTime("hora_inicio")).append("</td>")
                   .append("<td>").append(rs.getTime("hora_fin")).append("</td>")
                   .append("<td>").append(rs.getInt("cupo_maximo_actividad")).append("</td>")
                   .append("<td>").append(rs.getInt("asistentes")).append("</td>")
                   .append("</tr>");
            }

            html.append("</table>");
            html.append(pieHTML());

            guardarArchivoReporte(html.toString(), "reporte_actividades_" + codigoEvento);
        }
    }

    // Métodos auxiliares para generación de HTML
    private String encabezadoHTML(String titulo) {
        return "<!DOCTYPE html>" +
               "<html><head><title>" + titulo + "</title>" +
               "<style>body { font-family: Arial; margin: 20px; }" +
               "h1 { color: #2c3e50; }" +
               "table { border-collapse: collapse; width: 100%; }" +
               "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" +
               "th { background-color: #f2f2f2; }" +
               "</style></head><body>" +
               "<h1>" + titulo + "</h1>";
    }

    private String pieHTML() {
        return "<p>Reporte generado el: " + new java.util.Date() + "</p>" +
               "</body></html>";
    }

    private void guardarArchivoReporte(String contenido, String nombreBase) throws IOException {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String nombreArchivo = nombreBase + "_" + timestamp + ".html";
        Path archivoReporte = directorioSalida.resolve(nombreArchivo);

        // Si el archivo ya existe, agregar un sufijo incremental
        int contador = 1;
        while (Files.exists(archivoReporte)) {
            nombreArchivo = nombreBase + "_" + timestamp + "_" + contador + ".html";
            archivoReporte = directorioSalida.resolve(nombreArchivo);
            contador++;
        }

        // Guardar el archivo
        Files.write(archivoReporte, contenido.getBytes(StandardCharsets.UTF_8));
        agregarLog("   Reporte generado: " + nombreArchivo);
    }
}
