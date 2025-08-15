/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.eventos_hyrule.Vistas;

import com.eventos_hyrule.conexionDB;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/**
 *
 * @author sofia
 */
public class RegistroAsistenciaInternalFrame extends javax.swing.JInternalFrame {

    private JComboBox<EventoComboItem> cmbEventos;
    private JTextField txtBuscarEvento;
    private JComboBox<ActividadComboItem> cmbActividades;
    private JComboBox<ParticipanteComboItem> cmbParticipantes;
    private JButton btnRegistrarAsistencia;
    private JButton btnCancelar;
    private JTable tblAsistencias;
    private DefaultTableModel tableModel;
    
    private Connection connection;
    /**
     * Creates new form RegistroAsistenciaInternalFrame
     */    
    
    // Clases internas para manejar los items del combo con ID y texto
    private class EventoComboItem {
        private String codigo;
        private String texto;

        public EventoComboItem(String codigo, String texto) {
            this.codigo = codigo;
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }

        public String getCodigo() {
            return codigo;
        }
    }

    private class ActividadComboItem {
        private String codigo;
        private String texto;

        public ActividadComboItem(String codigo, String texto) {
            this.codigo = codigo;
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }

        public String getCodigo() {
            return codigo;
        }
    }

    private class ParticipanteComboItem {
        private String email;
        private String texto;

        public ParticipanteComboItem(String email, String texto) {
            this.email = email;
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }

        public String getEmail() {
            return email;
        }
    }
    
    public RegistroAsistenciaInternalFrame() {
        super("Registro de Asistencia", true, true, true, true);
        setSize(800, 600);
        
        conectarBaseDatos();
        
        initComponent();
        layoutComponents();
        cargarTodosEventos();
        //cargarAsistenciasRegistradas();
    }

    private void initComponent() {
        // Campo de búsqueda de eventos
        txtBuscarEvento = new JTextField(20);
        txtBuscarEvento.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarEventos();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarEventos();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarEventos();
            }
        });

        // Combo boxes
        cmbEventos = new JComboBox<>();
        cmbEventos.addActionListener(e -> {
            if (cmbEventos.getSelectedItem() != null) {
                String codigoEvento = ((EventoComboItem)cmbEventos.getSelectedItem()).getCodigo();
                cargarActividadesPorEvento(((EventoComboItem)cmbEventos.getSelectedItem()).getCodigo());

                if (cmbActividades.getSelectedItem() == null) {
                    tableModel.setRowCount(0);
                }
            }
        });
        
        cmbActividades = new JComboBox<>();
        cmbActividades.addActionListener(e -> {
            if (cmbActividades.getSelectedItem() != null && cmbEventos.getSelectedItem() != null) {
                String codigoEvento = ((EventoComboItem)cmbEventos.getSelectedItem()).getCodigo();
                String codigoActividad = ((ActividadComboItem)cmbActividades.getSelectedItem()).getCodigo();

                cargarParticipantesInscritosEnActividad(codigoEvento, codigoActividad);
                cargarAsistenciasRegistradas(codigoEvento, codigoActividad);
            } 
        });
        
        cmbParticipantes = new JComboBox<>();
        
        // Botones
        btnRegistrarAsistencia = new JButton("Registrar Asistencia");
        btnRegistrarAsistencia.addActionListener(e -> registrarAsistencia());
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        
        // Tabla de asistencias
        tableModel = new DefaultTableModel(
            new Object[]{"Evento", "Actividad", "Participante", "Fecha/Hora"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblAsistencias = new JTable(tableModel);
        tblAsistencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void layoutComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior para controles
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fila 0 - Búsqueda de evento
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(new JLabel("Buscar Evento:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        controlPanel.add(txtBuscarEvento, gbc);
        
        // Fila 1 - Selección de evento
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        controlPanel.add(new JLabel("Evento:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        controlPanel.add(cmbEventos, gbc);
        
        // Fila 2 - Selección de actividad
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        controlPanel.add(new JLabel("Actividad:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        controlPanel.add(cmbActividades, gbc);
        
        // Fila 3 - Selección de participante
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        controlPanel.add(new JLabel("Participante:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        controlPanel.add(cmbParticipantes, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(btnRegistrarAsistencia);
        buttonPanel.add(btnCancelar);
        
        // Agregar componentes al panel principal
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblAsistencias), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(panel);
    }

    private void cargarTodosEventos() {
        try {
            String sql = "SELECT codigo_evento, titulo_evento, fecha_evento FROM EVENTO ORDER BY fecha_evento DESC";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                cmbEventos.removeAllItems();
                while (rs.next()) {
                    String codigo = rs.getString("codigo_evento");
                    String texto = rs.getString("titulo_evento") + " (" + rs.getString("codigo_evento") + ") - " + 
                                  rs.getDate("fecha_evento").toString();
                    cmbEventos.addItem(new EventoComboItem(codigo, texto));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar eventos: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void filtrarEventos() {
        String filtro = txtBuscarEvento.getText().toLowerCase();
        EventoComboItem seleccionado = (EventoComboItem) cmbEventos.getSelectedItem();
        String codigoSeleccionado = seleccionado != null ? seleccionado.getCodigo() : null;
        
        cmbEventos.removeAllItems();
        
        try {
            String sql = "SELECT codigo_evento, titulo_evento, fecha_evento FROM EVENTO ORDER BY fecha_evento DESC";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                boolean mantieneSeleccion = false;
                while (rs.next()) {
                    String codigo = rs.getString("codigo_evento");
                    String texto = rs.getString("titulo_evento") + " (" + rs.getString("codigo_evento") + ") - " + 
                                  rs.getDate("fecha_evento").toString();
                    
                    if (filtro.isEmpty() || texto.toLowerCase().contains(filtro)) {
                        EventoComboItem item = new EventoComboItem(codigo, texto);
                        cmbEventos.addItem(item);
                        
                        if (codigoSeleccionado != null && codigoSeleccionado.equals(codigo)) {
                            cmbEventos.setSelectedItem(item);
                            mantieneSeleccion = true;
                        }
                    }
                }
                
                if (!mantieneSeleccion && cmbEventos.getItemCount() > 0) {
                    cmbEventos.setSelectedIndex(0);
                } else if (cmbEventos.getItemCount() == 0) {
                    cmbActividades.removeAllItems();
                    cmbParticipantes.removeAllItems();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al filtrar eventos: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cargarActividadesPorEvento(String codigoEvento) {
        try {
            String sql = "SELECT codigo_actividad, titulo_actividad FROM ACTIVIDAD " +
                         "WHERE codigo_evento = ? ORDER BY titulo_actividad";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, codigoEvento);
                ResultSet rs = pstmt.executeQuery();
                
                cmbActividades.removeAllItems();
                while (rs.next()) {
                    String codigo = rs.getString("codigo_actividad");
                    String texto = rs.getString("titulo_actividad") + " (" + rs.getString("codigo_actividad") + ")";
                    cmbActividades.addItem(new ActividadComboItem(codigo, texto));
                }
                
                if (cmbActividades.getItemCount() > 0) {
                    cmbActividades.setSelectedIndex(0);
                } else {
                    cmbParticipantes.removeAllItems();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar actividades: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cargarParticipantesInscritosEnActividad(String codigoEvento, String codigoActividad) {
        try {
            String sql = "SELECT p.email_participante, p.nombre_completo " +
                         "FROM PARTICIPANTE p " +
                         "JOIN INSCRIPCION i ON p.email_participante = i.email_participante " +
                         "JOIN ACTIVIDAD a ON i.codigo_evento = a.codigo_evento " +
                         "WHERE i.codigo_evento = ? AND a.codigo_actividad = ? AND i.validada = true " +
                         "ORDER BY p.nombre_completo";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, codigoEvento);
                pstmt.setString(2, codigoActividad);
                ResultSet rs = pstmt.executeQuery();
                
                cmbParticipantes.removeAllItems();
                while (rs.next()) {
                    String email = rs.getString("email_participante");
                    String texto = rs.getString("nombre_completo") + " (" + rs.getString("email_participante") + ")";
                    cmbParticipantes.addItem(new ParticipanteComboItem(email, texto));
                }
                
                if(cmbParticipantes.getItemCount() == 0){
                    JOptionPane.showMessageDialog(this,
                            "No hay participantes inscritos en esta actividad",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar participantes: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cargarAsistenciasRegistradas(String codigoEvento, String codigoActividad) {
        try {
            String sql = "SELECT e.titulo_evento AS evento, a.titulo_actividad AS actividad, " +
                          "p.nombre_completo AS participante, asi.fecha_asistencia " +
                          "FROM ASISTENCIA asi " +
                          "JOIN PARTICIPANTE p ON asi.email_participante = p.email_participante " +
                          "JOIN ACTIVIDAD a ON asi.codigo_actividad = a.codigo_actividad " +
                          "JOIN EVENTO e ON a.codigo_evento = e.codigo_evento " +
                          "WHERE a.codigo_evento = ? " +
                          "AND a.codigo_actividad = ? " +
                          "ORDER BY asi.fecha_asistencia DESC";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)){
                pstmt.setString(1, codigoEvento);
                pstmt.setString(2, codigoActividad);
                ResultSet rs = pstmt.executeQuery();
                
                tableModel.setRowCount(0);
                
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("evento"),
                        rs.getString("actividad"),
                        rs.getString("participante"),
                        rs.getTimestamp("fecha_asistencia")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar asistencias: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void registrarAsistencia() {
        if (cmbEventos.getSelectedItem() == null || 
            cmbActividades.getSelectedItem() == null || 
            cmbParticipantes.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar un evento, una actividad y un participante", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ParticipanteComboItem participante = (ParticipanteComboItem) cmbParticipantes.getSelectedItem();
        ActividadComboItem actividad = (ActividadComboItem) cmbActividades.getSelectedItem();
        EventoComboItem evento = (EventoComboItem) cmbEventos.getSelectedItem();

        try {
            // Verificar si ya existe esta asistencia
            String checkSql = "SELECT COUNT(*) FROM ASISTENCIA " +
                             "WHERE email_participante = ? AND codigo_actividad = ?";
            
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setString(1, participante.getEmail());
                checkStmt.setString(2, actividad.getCodigo());
                
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Este participante ya está registrado en esta actividad", 
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
                
            // Verificar que el participante está inscrito en la actividad
            if (!estaInscritoEnActividad(participante.getEmail(), actividad.getCodigo())) {
                JOptionPane.showMessageDialog(this, 
                    "El participante no está inscrito en esta actividad", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Insertar nueva asistencia
            String insertSql = "INSERT INTO ASISTENCIA (email_participante, codigo_actividad) VALUES (?, ?)";
            
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                insertStmt.setString(1, participante.getEmail());
                insertStmt.setString(2, actividad.getCodigo());
                
                int affectedRows = insertStmt.executeUpdate();
                
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Asistencia registrada exitosamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Actualizar la tabla
                    cargarAsistenciasRegistradas(evento.getCodigo(), actividad.getCodigo());
                    cargarParticipantesInscritosEnActividad(evento.getCodigo(), actividad.getCodigo());
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo registrar la asistencia", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar asistencia: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private boolean estaInscritoEnActividad(String emailParticipante, String codigoActividad) {
        try {
            String sql = "SELECT COUNT(*) FROM INSCRIPCION i " +
                         "JOIN ACTIVIDAD a ON i.codigo_evento = a.codigo_evento " +
                         "WHERE i.email_participante = ? " +
                         "AND a.codigo_actividad = ? " +
                         "AND i.validada = true";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, emailParticipante);
                pstmt.setString(2, codigoActividad);
                ResultSet rs = pstmt.executeQuery();
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
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
