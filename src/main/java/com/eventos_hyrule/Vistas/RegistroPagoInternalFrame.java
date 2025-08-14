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
import java.sql.SQLException;
import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;
/**
 *
 * @author sofia
 */
public class RegistroPagoInternalFrame extends JInternalFrame {
    private JComboBox<String> cmbParticipantes;
    private JComboBox<String> cmbEventos;
    private JComboBox<String> cmbMetodoPago;
    private JFormattedTextField txtMonto;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    
    private Connection connection;

    public RegistroPagoInternalFrame() {
        super("Registro de Pago", true, true, true, true);
        setSize(500, 350);
        
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
        cmbMetodoPago = new JComboBox<>(new String[]{"EFECTIVO", "TRANSFERENCIA", "TARJETA"});
        
        // Configurar campo de monto con formato numérico
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.0);
        formatter.setMaximum(9999.99);
        formatter.setAllowsInvalid(false);
        txtMonto = new JFormattedTextField(formatter);
        txtMonto.setColumns(10);
        txtMonto.setValue(0.0);
        
        btnRegistrar = new JButton("Registrar Pago");
        btnRegistrar.addActionListener(e -> registrarPago());
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
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
        
        // Fila 2 - Método de Pago
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Método de Pago:"), gbc);
        
        gbc.gridx = 1;
        panel.add(cmbMetodoPago, gbc);
        
        // Fila 3 - Monto
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Monto:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtMonto, gbc);
        
        // Fila 4 - Botones
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnRegistrar);
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

    private void registrarPago() {
        // Validar selecciones
        if (cmbParticipantes.getSelectedIndex() <= 0 || cmbEventos.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un participante y un evento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar monto
        double monto;
        try {
            monto = Double.parseDouble(txtMonto.getText().replace(",", ""));
            if (monto <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido mayor a cero", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Extraer datos de los combos
        String participanteSeleccionado = (String) cmbParticipantes.getSelectedItem();
        String emailParticipante = participanteSeleccionado.split(" - ")[0];
        
        String eventoSeleccionado = (String) cmbEventos.getSelectedItem();
        String codigoEvento = eventoSeleccionado.split(" - ")[0];
        
        // Verificar si ya existe inscripción
        if (!existeInscripcion(emailParticipante, codigoEvento)) {
            JOptionPane.showMessageDialog(this, "El participante no está inscrito en este evento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar si ya existe pago
        if (existePago(emailParticipante, codigoEvento)) {
            JOptionPane.showMessageDialog(this, "Ya existe un pago registrado para esta inscripción", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Registrar el pago como una transacción
        try {
            connection.setAutoCommit(false); // Iniciar transacción
            
            // 1. Registrar el pago
            String sqlPago = "INSERT INTO PAGO (email_participante, codigo_evento, metodo_pago, monto, fecha_pago) " +
                           "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
            
            try (PreparedStatement pstmtPago = connection.prepareStatement(sqlPago)) {
                pstmtPago.setString(1, emailParticipante);
                pstmtPago.setString(2, codigoEvento);
                pstmtPago.setString(3, (String) cmbMetodoPago.getSelectedItem());
                pstmtPago.setDouble(4, monto);
                
                int affectedRows = pstmtPago.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("No se pudo registrar el pago");
                }
            }
            
            // 2. Validar la inscripción automáticamente
            String sqlValidar = "UPDATE INSCRIPCION SET validada = true " +
                               "WHERE email_participante = ? AND codigo_evento = ?";
            
            try (PreparedStatement pstmtValidar = connection.prepareStatement(sqlValidar)) {
                pstmtValidar.setString(1, emailParticipante);
                pstmtValidar.setString(2, codigoEvento);
                
                int affectedRows = pstmtValidar.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("No se pudo validar la inscripción");
                }
            }
            
            connection.commit(); // Confirmar transacción
            JOptionPane.showMessageDialog(this, "Pago registrado e inscripción validada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (SQLException e) {
            try {
                connection.rollback(); // Revertir en caso de error
                JOptionPane.showMessageDialog(this, "Error en la transacción: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error grave al revertir transacción: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            try {
                connection.setAutoCommit(true); // Restaurar modo auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean existeInscripcion(String email, String codigoEvento) {
        String sql = "SELECT 1 FROM INSCRIPCION WHERE email_participante = ? AND codigo_evento = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean existePago(String email, String codigoEvento) {
        String sql = "SELECT 1 FROM PAGO WHERE email_participante = ? AND codigo_evento = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            
            return pstmt.executeQuery().next();
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
