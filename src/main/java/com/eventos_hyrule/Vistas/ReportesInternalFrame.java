/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.eventos_hyrule.Vistas;

import com.eventos_hyrule.conexionDB;
import com.eventos_hyrule.funciones.ParticipanteEvento;
import com.eventos_hyrule.funciones.ReporteEvento;
import com.eventos_hyrule.funciones.ReporteActividad;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
/**
 *
 * @author sofia
 */
public class ReportesInternalFrame extends javax.swing.JInternalFrame {
    private JTabbedPane tabbedPane;
    private Connection connection;
    
    // Panel para Reporte de Participantes
    private JPanel panelParticipantes;
    private JComboBox<String> cmbEventosParticipantes;
    private JComboBox<String> cmbTipoParticipante;
    private JTextField txtInstitucionParticipantes;
    private JButton btnGenerarParticipantes;
    
    // Panel para Reporte de Actividades
    private JPanel panelActividades;
    private JComboBox<String> cmbEventosActividades;
    private JComboBox<String> cmbTipoActividad;
    private JTextField txtEncargadoActividades;
    private JButton btnGenerarActividades;
    
    // Panel para Reporte de Eventos
    private JPanel panelEventos;
    private JComboBox<String> cmbTipoEvento;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTextField txtCupoMinimo;
    private JTextField txtCupoMaximo;
    private JButton btnGenerarEventos;
    
    private JFileChooser fileChooser;
    /**
     * Creates new form ReportesInternalFrame
     */
    public ReportesInternalFrame() {
        super("Generación de Reportes", true, true, true, true);
        setSize(900, 600);
        conectarBaseDatos();
        
        initComponent();
        layoutComponents();
        cargarDatosIniciales();
    }

    private void initComponent() {
        // Configurar el file chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos HTML", "html"));
        
        // Inicializar componentes para Reporte de Participantes
        cmbEventosParticipantes = new JComboBox<>();
        cmbTipoParticipante = new JComboBox<>(new String[]{"", "ESTUDIANTE", "PROFESIONAL", "INVITADO"});
        txtInstitucionParticipantes = new JTextField(20);
        btnGenerarParticipantes = new JButton("Generar Reporte");
        btnGenerarParticipantes.addActionListener(e -> generarReporteParticipantes());
        
        // Inicializar componentes para Reporte de Actividades
        cmbEventosActividades = new JComboBox<>();
        cmbTipoActividad = new JComboBox<>(new String[]{"", "CHARLA", "TALLER", "DEBATE", "OTRA"});
        txtEncargadoActividades = new JTextField(20);
        btnGenerarActividades = new JButton("Generar Reporte");
        btnGenerarActividades.addActionListener(e -> generarReporteActividades());
        
        // Inicializar componentes para Reporte de Eventos
        cmbTipoEvento = new JComboBox<>(new String[]{"", "CHARLA", "CONGRESO", "TALLER", "DEBATE"});
        txtFechaInicio = new JTextField(10);
        txtFechaFin = new JTextField(10);
        txtCupoMinimo = new JTextField(5);
        txtCupoMaximo = new JTextField(5);
        btnGenerarEventos = new JButton("Generar Reporte");
        btnGenerarEventos.addActionListener(e -> generarReporteEventos());
    }

    private void layoutComponents() {
        tabbedPane = new JTabbedPane();
        
        // Panel de Reporte de Participantes
        panelParticipantes = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panelParticipantes.add(new JLabel("Evento:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelParticipantes.add(cmbEventosParticipantes, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panelParticipantes.add(new JLabel("Tipo de Participante:"), gbc);
        gbc.gridx = 1;
        panelParticipantes.add(cmbTipoParticipante, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panelParticipantes.add(new JLabel("Institución:"), gbc);
        gbc.gridx = 1;
        panelParticipantes.add(txtInstitucionParticipantes, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panelParticipantes.add(btnGenerarParticipantes, gbc);
        
        // Panel de Reporte de Actividades
        panelActividades = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panelActividades.add(new JLabel("Evento:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelActividades.add(cmbEventosActividades, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panelActividades.add(new JLabel("Tipo de Actividad:"), gbc);
        gbc.gridx = 1;
        panelActividades.add(cmbTipoActividad, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panelActividades.add(new JLabel("Encargado (email):"), gbc);
        gbc.gridx = 1;
        panelActividades.add(txtEncargadoActividades, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panelActividades.add(btnGenerarActividades, gbc);
        
        // Panel de Reporte de Eventos
        panelEventos = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panelEventos.add(new JLabel("Tipo de Evento:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelEventos.add(cmbTipoEvento, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panelEventos.add(new JLabel("Fecha Inicio (dd/mm/aaaa):"), gbc);
        gbc.gridx = 1;
        panelEventos.add(txtFechaInicio, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panelEventos.add(new JLabel("Fecha Fin (dd/mm/aaaa):"), gbc);
        gbc.gridx = 1;
        panelEventos.add(txtFechaFin, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panelEventos.add(new JLabel("Cupo Mínimo:"), gbc);
        gbc.gridx = 1;
        panelEventos.add(txtCupoMinimo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panelEventos.add(new JLabel("Cupo Máximo:"), gbc);
        gbc.gridx = 1;
        panelEventos.add(txtCupoMaximo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panelEventos.add(btnGenerarEventos, gbc);
        
        // Agregar pestañas
        tabbedPane.addTab("Participantes", panelParticipantes);
        tabbedPane.addTab("Actividades", panelActividades);
        tabbedPane.addTab("Eventos", panelEventos);
        
        setContentPane(tabbedPane);
    }

    private void cargarDatosIniciales() {
        cargarEventosComboBox(cmbEventosParticipantes);
        cargarEventosComboBox(cmbEventosActividades);
    }

    private void cargarEventosComboBox(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        comboBox.addItem(""); // Item vacío para no filtrar
        
        try {
            String sql = "SELECT codigo_evento, titulo_evento FROM EVENTO ORDER BY titulo_evento";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    comboBox.addItem(rs.getString("titulo_evento") + " (" + rs.getString("codigo_evento") + ")");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar eventos: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void generarReporteParticipantes() {
        String selectedEvent = (String) cmbEventosParticipantes.getSelectedItem();
        if (selectedEvent == null || selectedEvent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un evento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Extraer código del evento del texto seleccionado
        String codigoEvento = selectedEvent.substring(selectedEvent.lastIndexOf("(") + 1, selectedEvent.lastIndexOf(")"));
        String tipoParticipante = (String) cmbTipoParticipante.getSelectedItem();
        String institucion = txtInstitucionParticipantes.getText().trim();
        
        try {
            // Construir consulta SQL
            String sql = "SELECT p.email_participante, p.tipo_participante, p.nombre_completo, p.institucion, i.validada " +
                         "FROM PARTICIPANTE p " +
                         "JOIN INSCRIPCION i ON p.email_participante = i.email_participante " +
                         "WHERE i.codigo_evento = ?";
            
            List<String> conditions = new ArrayList<>();
            List<Object> params = new ArrayList<>();
            params.add(codigoEvento);
            
            if (tipoParticipante != null && !tipoParticipante.isEmpty()) {
                conditions.add("p.tipo_participante = ?");
                params.add(tipoParticipante);
            }
            
            if (!institucion.isEmpty()) {
                conditions.add("p.institucion LIKE ?");
                params.add("%" + institucion + "%");
            }
            
            if (!conditions.isEmpty()) {
                sql += " AND " + String.join(" AND ", conditions);
            }
            
            sql += " ORDER BY p.nombre_completo";
            
            // Ejecutar consulta
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }
                
                ResultSet rs = pstmt.executeQuery();
                
                // Generar HTML
                StringBuilder html = new StringBuilder();
                html.append("<!DOCTYPE html>\n<html>\n<head>\n")
                   .append("<title>Reporte de Participantes</title>\n")
                   .append("<style>\n")
                   .append("body { font-family: Arial, sans-serif; }\n")
                   .append("h1 { color: #2c3e50; }\n")
                   .append("table { width: 100%; border-collapse: collapse; }\n")
                   .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n")
                   .append("th { background-color: #f2f2f2; }\n")
                   .append("tr:nth-child(even) { background-color: #f9f9f9; }\n")
                   .append("</style>\n</head>\n<body>\n")
                   .append("<h1>Reporte de Participantes</h1>\n")
                   .append("<p><strong>Evento:</strong> ").append(selectedEvent).append("</p>\n")
                   .append("<table>\n")
                   .append("<tr><th>Correo Electrónico</th><th>Tipo</th><th>Nombre Completo</th><th>Institución</th><th>Validado</th></tr>\n");
                
                while (rs.next()) {
                    html.append("<tr>")
                       .append("<td>").append(rs.getString("email_participante")).append("</td>")
                       .append("<td>").append(rs.getString("tipo_participante")).append("</td>")
                       .append("<td>").append(rs.getString("nombre_completo")).append("</td>")
                       .append("<td>").append(rs.getString("institucion")).append("</td>")
                       .append("<td>").append(rs.getBoolean("validada") ? "Sí" : "No").append("</td>")
                       .append("</tr>\n");
                }
                
                html.append("</table>\n")
                   .append("<p>Generado el: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())).append("</p>\n")
                   .append("</body>\n</html>");
                
                // Guardar archivo
                guardarArchivoHTML(html.toString(), "reporte_participantes");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void generarReporteActividades() {
        String selectedEvent = (String) cmbEventosActividades.getSelectedItem();
        if (selectedEvent == null || selectedEvent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un evento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String codigoEvento = selectedEvent.substring(selectedEvent.lastIndexOf("(") + 1, selectedEvent.lastIndexOf(")"));
        String tipoActividad = (String) cmbTipoActividad.getSelectedItem();
        String encargado = txtEncargadoActividades.getText().trim();

        try {
            String sql = "SELECT a.codigo_actividad, a.codigo_evento, a.titulo_actividad, " +
                        "p.nombre_completo AS nombre_encargado, a.hora_inicio, a.hora_fin, " +
                        "a.cupo_maximo_actividad, COUNT(asi.email_participante) AS participantes_asistencia " +
                        "FROM ACTIVIDAD a " +
                        "LEFT JOIN ASISTENCIA asi ON a.codigo_actividad = asi.codigo_actividad " +
                        "JOIN PARTICIPANTE p ON a.email_encargado = p.email_participante " +
                        "WHERE a.codigo_evento = ?";

            List<String> conditions = new ArrayList<>();
            List<Object> params = new ArrayList<>();
            params.add(codigoEvento);

            if (tipoActividad != null && !tipoActividad.isEmpty()) {
                conditions.add("a.tipo_actividad = ?");
                params.add(tipoActividad);
            }

            if (!encargado.isEmpty()) {
                conditions.add("a.email_encargado LIKE ?");
                params.add("%" + encargado + "%");
            }

            sql += conditions.isEmpty() ? "" : " AND " + String.join(" AND ", conditions);
            sql += " GROUP BY a.codigo_actividad, a.codigo_evento, a.titulo_actividad, p.nombre_completo, " +
                  "a.hora_inicio, a.hora_fin, a.cupo_maximo_actividad " +
                  "ORDER BY a.hora_inicio";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }

                ResultSet rs = pstmt.executeQuery();
                List<ReporteActividad> actividades = new ArrayList<>();

                while (rs.next()) {
                    actividades.add(new ReporteActividad(
                        rs.getString("codigo_actividad"),
                        rs.getString("codigo_evento"),
                        rs.getString("titulo_actividad"),
                        rs.getString("nombre_encargado"),
                        rs.getTime("hora_inicio"),
                        rs.getTime("hora_fin"),
                        rs.getInt("cupo_maximo_actividad"),
                        rs.getInt("participantes_asistencia")
                    ));
                }

                // Generar HTML
                StringBuilder html = new StringBuilder();
                html.append("<!DOCTYPE html>\n<html>\n<head>\n")
                   .append("<title>Reporte de Actividades</title>\n")
                   .append(estilosCSS())
                   .append("</head>\n<body>\n")
                   .append("<h1>Reporte de Actividades</h1>\n")
                   .append("<p><strong>Evento:</strong> ").append(selectedEvent).append("</p>\n")
                   .append("<table>\n")
                   .append("<tr><th>Código</th><th>Título</th><th>Encargado</th><th>Hora Inicio</th>")
                   .append("<th>Hora Fin</th><th>Cupo Máximo</th><th>Participantes</th></tr>\n");

                for (ReporteActividad actividad : actividades) {
                    html.append("<tr>")
                       .append("<td>").append(actividad.getCodigoActividad()).append("</td>")
                       .append("<td>").append(actividad.getTitulo()).append("</td>")
                       .append("<td>").append(actividad.getNombreEncargado()).append("</td>")
                       .append("<td>").append(actividad.getHoraInicio()).append("</td>")
                       .append("<td>").append(actividad.getHoraFin()).append("</td>")
                       .append("<td>").append(actividad.getCupoMaximo()).append("</td>")
                       .append("<td>").append(actividad.getParticipantesAsistencia()).append("</td>")
                       .append("</tr>\n");
                }

                html.append("</table>\n")
                   .append(pieDePagina())
                   .append("</body>\n</html>");

                guardarYMostrarReporte(html.toString(), "reporte_actividades");
            }
        } catch (SQLException ex) {
            manejarErrorSQL(ex);
        }
    }

    private void generarReporteEventos() {
        String tipoEvento = (String) cmbTipoEvento.getSelectedItem();
        String fechaInicio = txtFechaInicio.getText().trim();
        String fechaFin = txtFechaFin.getText().trim();
        String cupoMinimoStr = txtCupoMinimo.getText().trim();
        String cupoMaximoStr = txtCupoMaximo.getText().trim();

        // Validar fechas
        if ((!fechaInicio.isEmpty() && fechaFin.isEmpty()) || 
            (fechaInicio.isEmpty() && !fechaFin.isEmpty())) {
            JOptionPane.showMessageDialog(this, 
                "Debe especificar ambas fechas o ninguna", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar cupos
        int cupoMinimo = -1, cupoMaximo = -1;
        try {
            if (!cupoMinimoStr.isEmpty()) cupoMinimo = Integer.parseInt(cupoMinimoStr);
            if (!cupoMaximoStr.isEmpty()) cupoMaximo = Integer.parseInt(cupoMaximoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Los valores de cupo deben ser números enteros", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Consulta para eventos
            String sql = "SELECT e.codigo_evento, e.fecha_evento, e.titulo_evento, e.tipo_evento, " +
                        "e.ubicacion_evento, e.cupo_maximo_evento, " +
                        "SUM(CASE WHEN i.validada THEN 1 ELSE 0 END) AS participantes_validados, " +
                        "SUM(CASE WHEN NOT i.validada THEN 1 ELSE 0 END) AS participantes_no_validados, " +
                        "COALESCE(SUM(pg.monto), 0) AS monto_total " +
                        "FROM EVENTO e " +
                        "LEFT JOIN INSCRIPCION i ON e.codigo_evento = i.codigo_evento " +
                        "LEFT JOIN PAGO pg ON i.email_participante = pg.email_participante AND i.codigo_evento = pg.codigo_evento " +
                        "WHERE 1=1";

            List<String> conditions = new ArrayList<>();
            List<Object> params = new ArrayList<>();

            if (tipoEvento != null && !tipoEvento.isEmpty()) {
                conditions.add("e.tipo_evento = ?");
                params.add(tipoEvento);
            }

            if (!fechaInicio.isEmpty() && !fechaFin.isEmpty()) {
                conditions.add("e.fecha_evento BETWEEN ? AND ?");
                try {
                    params.add(new java.sql.Date(new SimpleDateFormat("dd/MM/yyyy").parse(fechaInicio).getTime()));
                    params.add(new java.sql.Date(new SimpleDateFormat("dd/MM/yyyy").parse(fechaFin).getTime()));
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Formato de fecha inválido. Use dd/mm/aaaa", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (cupoMinimo >= 0) {
                conditions.add("e.cupo_maximo_evento >= ?");
                params.add(cupoMinimo);
            }

            if (cupoMaximo >= 0) {
                conditions.add("e.cupo_maximo_evento <= ?");
                params.add(cupoMaximo);
            }

            sql += conditions.isEmpty() ? "" : " AND " + String.join(" AND ", conditions);
            sql += " GROUP BY e.codigo_evento, e.fecha_evento, e.titulo_evento, e.tipo_evento, e.ubicacion_evento, e.cupo_maximo_evento " +
                   "ORDER BY e.fecha_evento DESC";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }

                ResultSet rs = pstmt.executeQuery();
                List<ReporteEvento> eventos = new ArrayList<>();

                while (rs.next()) {
                    ReporteEvento evento = new ReporteEvento(
                        rs.getString("codigo_evento"),
                        rs.getDate("fecha_evento"),
                        rs.getString("titulo_evento"),
                        rs.getString("tipo_evento"),
                        rs.getString("ubicacion_evento"),
                        rs.getInt("cupo_maximo_evento"),
                        rs.getDouble("monto_total"),
                        rs.getInt("participantes_validados"),
                        rs.getInt("participantes_no_validados")
                    );

                    // Obtener participantes para este evento
                    cargarParticipantesEvento(evento);
                    eventos.add(evento);
                }

                // Generar HTML
                StringBuilder html = new StringBuilder();
                html.append("<!DOCTYPE html>\n<html>\n<head>\n")
                   .append("<title>Reporte de Eventos</title>\n")
                   .append(estilosCSS())
                   .append("</head>\n<body>\n")
                   .append("<h1>Reporte de Eventos</h1>\n");

                for (ReporteEvento evento : eventos) {
                    html.append("<div class='evento'>")
                       .append("<h2>").append(evento.getTitulo()).append("</h2>")
                       .append("<p><strong>Código:</strong> ").append(evento.getCodigoEvento()).append("</p>")
                       .append("<p><strong>Fecha:</strong> ").append(evento.getFecha()).append("</p>")
                       .append("<p><strong>Tipo:</strong> ").append(evento.getTipoEvento()).append("</p>")
                       .append("<p><strong>Ubicación:</strong> ").append(evento.getUbicacion()).append("</p>")
                       .append("<p><strong>Cupo Máximo:</strong> ").append(evento.getCupoMaximo()).append("</p>")
                       .append("<h3>Participantes</h3>")
                       .append("<table>")
                       .append("<tr><th>Nombre</th><th>Email</th><th>Tipo</th><th>Método Pago</th><th>Monto</th></tr>");

                    for (ParticipanteEvento participante : evento.getParticipantes()) {
                        html.append("<tr>")
                           .append("<td>").append(participante.getNombre()).append("</td>")
                           .append("<td>").append(participante.getEmail()).append("</td>")
                           .append("<td>").append(participante.getTipoParticipante()).append("</td>")
                           .append("<td>").append(participante.getMetodoPago()).append("</td>")
                           .append("<td>").append(String.format("Q%.2f", participante.getMontoPagado())).append("</td>")
                           .append("</tr>");
                    }

                    html.append("</table>")
                       .append("<p><strong>Total Recaudado:</strong> Q").append(String.format("%.2f", evento.getMontoTotal())).append("</p>")
                       .append("<p><strong>Participantes Validados:</strong> ").append(evento.getParticipantesValidados()).append("</p>")
                       .append("<p><strong>Participantes No Validados:</strong> ").append(evento.getParticipantesNoValidados()).append("</p>")
                       .append("</div><hr>");
                }

                html.append(pieDePagina())
                   .append("</body>\n</html>");

                guardarYMostrarReporte(html.toString(), "reporte_eventos");
            }
        } catch (SQLException ex) {
            manejarErrorSQL(ex);
        }
    }
    
    private void cargarParticipantesEvento(ReporteEvento evento) throws SQLException {
        String sql = "SELECT p.email_participante, p.nombre_completo, p.tipo_participante, " +
                    "pg.metodo_pago, pg.monto " +
                    "FROM PARTICIPANTE p " +
                    "JOIN INSCRIPCION i ON p.email_participante = i.email_participante " +
                    "LEFT JOIN PAGO pg ON p.email_participante = pg.email_participante AND i.codigo_evento = pg.codigo_evento " +
                    "WHERE i.codigo_evento = ? AND i.validada = true " +
                    "ORDER BY p.nombre_completo";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, evento.getCodigoEvento());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                evento.getParticipantes().add(new ParticipanteEvento(
                    rs.getString("email_participante"),
                    rs.getString("nombre_completo"),
                    rs.getString("tipo_participante"),
                    rs.getString("metodo_pago"),
                    rs.getDouble("monto")
                ));
            }
        }
    }
    
    private String estilosCSS() {
        return "<style>\n" +
              "body { font-family: Arial, sans-serif; margin: 20px; }\n" +
              "h1, h2, h3 { color: #2c3e50; }\n" +
              "table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }\n" +
              "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n" +
              "th { background-color: #f2f2f2; }\n" +
              "tr:nth-child(even) { background-color: #f9f9f9; }\n" +
              ".evento { margin-bottom: 30px; }\n" +
              "hr { margin: 20px 0; border: 0; border-top: 1px solid #eee; }\n" +
              "</style>\n";
    }

    private String pieDePagina() {
        return "<p style='margin-top: 30px; font-size: 0.9em; color: #777;'>" +
              "Reporte generado el " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) +
              "</p>\n";
    }
    
    private void guardarArchivoHTML(String contenido, String nombreBase) {
        fileChooser.setSelectedFile(new File(nombreBase + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".html"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Asegurar extensión .html
            if (!file.getName().toLowerCase().endsWith(".html")) {
                file = new File(file.getAbsolutePath() + ".html");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(contenido);
                JOptionPane.showMessageDialog(this, 
                    "Reporte generado exitosamente en:\n" + file.getAbsolutePath(), 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Opción para abrir el reporte automáticamente
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(file.toURI());
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al guardar el archivo: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void guardarYMostrarReporte(String contenido, String nombreBase) {
        fileChooser.setSelectedFile(new File(nombreBase + "_" + 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".html"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Asegurar extensión .html
            if (!file.getName().toLowerCase().endsWith(".html")) {
                file = new File(file.getAbsolutePath() + ".html");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(contenido);

                // Mostrar diálogo con opción para abrir el reporte
                Object[] options = {"Abrir Reporte", "Cerrar"};
                int opcion = JOptionPane.showOptionDialog(this,
                    "Reporte generado exitosamente en:\n" + file.getAbsolutePath(),
                    "Éxito",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

                if (opcion == 0) {
                    Desktop.getDesktop().browse(file.toURI());
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al guardar/abrir el archivo: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void manejarErrorSQL(SQLException ex) {
        JOptionPane.showMessageDialog(this, 
            "Error al generar reporte: " + ex.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    
        private void conectarBaseDatos() {
        try {
            conexionDB conexion = new conexionDB();
            conexion.Connect();
            this.connection = conexion.getConnection();
            
            if (this.connection == null || this.connection.isClosed()) {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo establecer conexión con la base de datos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al conectar con la base de datos: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            dispose();
        }
    }

    @Override
    public void dispose() {
        // Cerrar la conexión al cerrar la ventana
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        super.dispose();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 394, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
