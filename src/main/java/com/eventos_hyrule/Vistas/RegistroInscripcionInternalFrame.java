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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistroInscripcionInternalFrame extends JInternalFrame {
    private JComboBox<String> cmbParticipantes;
    private JComboBox<String> cmbEventos;
    //private JComboBox<String> cmbTipoInscripcion;
    //private JButton btnRegistrar;
    private JButton btnCancelar;
    //private JButton btnValidar;
    private JLabel lblEstado;
    private JLabel lblEstadoPago;
    
    private Connection connection;

    public RegistroInscripcionInternalFrame() {
        super("Gestión de Inscripciones", true, true, true, true);
        setSize(650, 350);
        
        // Obtener conexión a la base de datos
        conexionDB conexion = new conexionDB();
        conexion.Connect();
        this.connection = conexion.getConnection();
        
        initComponent();
        layoutComponents();
        cargarParticipantes();
        cargarEventos();
    }

    private void initComponent() {
        cmbParticipantes = new JComboBox<>();
        cmbEventos = new JComboBox<>();
        cmbTipoInscripcion = new JComboBox<>(new String[]{"ASISTENTE", "CONFERENCISTA", "TALLERISTA", "OTRO"});
        
        btnRegistrar = new JButton("Registrar Inscripción");
        btnRegistrar.addActionListener(e -> registrarInscripcion());
        
        btnValidar = new JButton("Validar Inscripción");
        btnValidar.addActionListener(e -> validarInscripcion());
        btnValidar.setEnabled(false);
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        
        lblEstado = new JLabel(" ");
        lblEstado.setForeground(Color.BLUE);
        
        lblEstadoPago = new JLabel(" ");
        lblEstadoPago.setForeground(new Color(0, 100, 0)); // Verde oscuro
        
        // Listener para actualizar estado cuando cambia la selección
        cmbParticipantes.addActionListener(e -> actualizarEstadoInscripcion());
        cmbEventos.addActionListener(e -> actualizarEstadoInscripcion());
    }

    private void layoutComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fila 0 - Participante
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Participante:"), gbc);
        
        gbc.gridx = 1;
        panel.add(cmbParticipantes, gbc);
        
        // Fila 1 - Evento
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Evento:"), gbc);
        
        gbc.gridx = 1;
        panel.add(cmbEventos, gbc);
        
        // Fila 2 - Tipo Inscripción
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Tipo Inscripción:"), gbc);
        
        gbc.gridx = 1;
        panel.add(cmbTipoInscripcion, gbc);
        
        // Fila 3 - Estado Pago
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Estado de Pago:"), gbc);
        
        gbc.gridx = 1;
        panel.add(lblEstadoPago, gbc);
        
        // Fila 4 - Estado Inscripción
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Estado Inscripción:"), gbc);
        
        gbc.gridx = 1;
        panel.add(lblEstado, gbc);
        
        // Fila 5 - Botones
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnRegistrar);
        //buttonPanel.add(btnValidar);
        buttonPanel.add(btnCancelar);
        panel.add(buttonPanel, gbc);
        
        setContentPane(panel);
    }

    private void cargarParticipantes() {
        try {
            String sql = "SELECT email_participante, nombre_completo FROM PARTICIPANTE ORDER BY nombre_completo";
            var stmt = connection.createStatement();
            var rs = stmt.executeQuery(sql);
            
            cmbParticipantes.removeAllItems();
            cmbParticipantes.addItem("-- Seleccione participante --");
            while (rs.next()) {
                cmbParticipantes.addItem(rs.getString("email_participante") + " - " + rs.getString("nombre_completo"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar participantes: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarEventos() {
        try {
            String sql = "SELECT codigo_evento, titulo_evento FROM EVENTO ORDER BY fecha_evento";
            var stmt = connection.createStatement();
            var rs = stmt.executeQuery(sql);
            
            cmbEventos.removeAllItems();
            cmbEventos.addItem("-- Seleccione evento --");
            while (rs.next()) {
                cmbEventos.addItem(rs.getString("codigo_evento") + " - " + rs.getString("titulo_evento"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar eventos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEstadoInscripcion() {
        if (cmbParticipantes.getSelectedIndex() <= 0 || cmbEventos.getSelectedIndex() <= 0) {
            lblEstado.setText(" ");
            lblEstadoPago.setText(" ");
            btnValidar.setEnabled(false);
            return;
        }
        
        String participanteSeleccionado = (String) cmbParticipantes.getSelectedItem();
        String emailParticipante = participanteSeleccionado.split(" - ")[0];
        
        String eventoSeleccionado = (String) cmbEventos.getSelectedItem();
        String codigoEvento = eventoSeleccionado.split(" - ")[0];
        
        // Verificar estado de la inscripción
        try {
            String sqlInscripcion = "SELECT validada FROM INSCRIPCION " +
                                  "WHERE email_participante = ? AND codigo_evento = ?";
            PreparedStatement pstmt = connection.prepareStatement(sqlInscripcion);
            pstmt.setString(1, emailParticipante);
            pstmt.setString(2, codigoEvento);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                boolean validada = rs.getBoolean("validada");
                lblEstado.setText(validada ? "Validada" : "Pendiente de validación");
                btnRegistrar.setEnabled(false);
                
                // Verificar estado de pago
                String sqlPago = "SELECT 1 FROM PAGO " +
                               "WHERE email_participante = ? AND codigo_evento = ?";
                PreparedStatement pstmtPago = connection.prepareStatement(sqlPago);
                pstmtPago.setString(1, emailParticipante);
                pstmtPago.setString(2, codigoEvento);
                
                ResultSet rsPago = pstmtPago.executeQuery();
                if (rsPago.next()) {
                    lblEstadoPago.setText("Pago registrado");
                    if (!validada) {
                        btnValidar.setEnabled(true);
                    }
                } else {
                    lblEstadoPago.setText("Pago pendiente");
                    btnValidar.setEnabled(false);
                }
            } else {
                lblEstado.setText("No inscrito");
                lblEstadoPago.setText(" ");
                btnRegistrar.setEnabled(true);
                btnValidar.setEnabled(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void registrarInscripcion() {
        if (cmbParticipantes.getSelectedIndex() <= 0 || cmbEventos.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un participante y un evento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Extraer email y código de evento de los combos
        String participanteSeleccionado = (String) cmbParticipantes.getSelectedItem();
        String emailParticipante = participanteSeleccionado.split(" - ")[0];
        
        String eventoSeleccionado = (String) cmbEventos.getSelectedItem();
        String codigoEvento = eventoSeleccionado.split(" - ")[0];
        
        // Verificar cupo disponible
        if (!hayCupoDisponible(codigoEvento)) {
            JOptionPane.showMessageDialog(this, "No hay cupo disponible para este evento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Registrar la inscripción
        String sql = "INSERT INTO INSCRIPCION (email_participante, codigo_evento, tipo_inscripcion, validada) " +
                     "VALUES (?, ?, ?, false)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emailParticipante);
            pstmt.setString(2, codigoEvento);
            pstmt.setString(3, (String) cmbTipoInscripcion.getSelectedItem());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                lblEstado.setText("Pendiente de validación");
                lblEstadoPago.setText("Pago pendiente");
                btnRegistrar.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Inscripción registrada. Debe registrar un pago para validarla.", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar la inscripción", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                JOptionPane.showMessageDialog(this, "Este participante ya está inscrito en el evento seleccionado", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar inscripción: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        actualizarEstadoInscripcion();
    }

    private void validarInscripcion() {
        String participanteSeleccionado = (String) cmbParticipantes.getSelectedItem();
        String emailParticipante = participanteSeleccionado.split(" - ")[0];
        
        String eventoSeleccionado = (String) cmbEventos.getSelectedItem();
        String codigoEvento = eventoSeleccionado.split(" - ")[0];
        
        // Validar la inscripción
        String sql = "UPDATE INSCRIPCION SET validada = true " +
                     "WHERE email_participante = ? AND codigo_evento = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emailParticipante);
            pstmt.setString(2, codigoEvento);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                lblEstado.setText("Validada");
                btnValidar.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Inscripción validada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo validar la inscripción", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al validar inscripción: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        actualizarEstadoInscripcion();
    }

    private boolean hayCupoDisponible(String codigoEvento) {
        String sql = "SELECT e.cupo_maximo_evento, COUNT(i.email_participante) as inscritos " +
                     "FROM EVENTO e LEFT JOIN INSCRIPCION i ON e.codigo_evento = i.codigo_evento " +
                     "WHERE e.codigo_evento = ? " +
                     "GROUP BY e.codigo_evento";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, codigoEvento);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int cupoMaximo = rs.getInt("cupo_maximo_evento");
                int inscritos = rs.getInt("inscritos");
                return inscritos < cupoMaximo;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmbTipoInscripcion = new JComboBox<>(new String []{"ASISTENTE", "CONFERENCISTA", "TALLERISTA", "OTRO"});
        btnRegistrar = new javax.swing.JButton();
        btnValidar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        cmbTipoInscripcion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnRegistrar.setText("Registrar Inscripción");

        btnValidar.setText("Validar Inscripción");
        btnValidar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnValidarActionPerformed(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(102, 204, 255));
        jLabel1.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmbTipoInscripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRegistrar)
                .addGap(105, 105, 105)
                .addComponent(btnValidar)
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addComponent(cmbTipoInscripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrar)
                    .addComponent(btnValidar)
                    .addComponent(jLabel1))
                .addGap(36, 36, 36))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnValidarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnValidarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JButton btnValidar;
    private javax.swing.JComboBox<String> cmbTipoInscripcion;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
