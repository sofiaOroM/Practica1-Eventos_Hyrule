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
/**
 *
 * @author sofia
 */
public class RegistroParticipanteInternalFrame extends javax.swing.JInternalFrame {
    private JTextField txtNombre;
    private JComboBox<String> cmbTipoParticipante;
    private JTextField txtInstitucion;
    private JTextField txtEmail;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    private Connection connection;
    
    /**
     * Creates new form RegistroParticipanteInternalFrame
     */
    public RegistroParticipanteInternalFrame() {
        super("Registro de Participante", true, true, true, true);
        setSize(500, 350);
        
        // Obtener conexión a la base de datos
        conexionDB conexion = new conexionDB();
        conexion.Connect();
        this.connection = conexion.getConnection();
        
        initComponent();
        layoutComponents();
    }
    private void initComponent() {
        txtNombre = new JTextField(30);
        cmbTipoParticipante = new JComboBox<>(new String[]{"ESTUDIANTE", "PROFESIONAL", "INVITADO"});
        txtInstitucion = new JTextField(30);
        txtEmail = new JTextField(30);
        
        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarParticipante();
            }
        });
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    private void layoutComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fila 0 - Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre Completo:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtNombre, gbc);
        
        // Fila 1 - Tipo Participante
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Tipo de Participante:"), gbc);
        
        gbc.gridx = 1;
        panel.add(cmbTipoParticipante, gbc);
        
        // Fila 2 - Institución
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Institución:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtInstitucion, gbc);
        
        // Fila 3 - Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Correo Electrónico:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);
        
        // Fila 4 - Botones
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        panel.add(buttonPanel, gbc);
        
        setContentPane(panel);
    }
    private void guardarParticipante() {
        // Validaciones
        if (txtNombre.getText().isEmpty() || txtInstitucion.getText().isEmpty() || txtEmail.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar longitud del nombre
        if (txtNombre.getText().length() > 45) {
            JOptionPane.showMessageDialog(this, "El nombre no puede exceder 45 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar longitud de institución
        if (txtInstitucion.getText().length() > 150) {
            JOptionPane.showMessageDialog(this, "La institución no puede exceder 150 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar formato de email
        if (!txtEmail.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this, "Ingrese un correo electrónico válido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar si el correo ya existe
        if (correoYaRegistrado(txtEmail.getText())) {
            JOptionPane.showMessageDialog(this, "El correo electrónico ya está registrado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Insertar en la base de datos
        String sql = "INSERT INTO PARTICIPANTE (email_participante, nombre_completo, tipo_participante, institucion) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, txtEmail.getText());
            pstmt.setString(2, txtNombre.getText());
            pstmt.setString(3, (String) cmbTipoParticipante.getSelectedItem());
            pstmt.setString(4, txtInstitucion.getText());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Participante registrado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el participante", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // Violación de clave única
                JOptionPane.showMessageDialog(this, "El correo electrónico ya está registrado", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void limpiarCampos() {
        txtNombre.setText("");
        txtInstitucion.setText("");
        txtEmail.setText("");
        cmbTipoParticipante.setSelectedIndex(0);
    }
    
    private boolean correoYaRegistrado(String email) {
        String sql = "SELECT COUNT(*) FROM PARTICIPANTE WHERE email_participante = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar correo en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false; // Por defecto asumimos que no existe si hay error
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
