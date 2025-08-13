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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author sofia
 */
public class RegistroEventoInternalFrame extends JInternalFrame {
    private JTextField txtCodigo;
    private JTextField txtFecha;
    private JComboBox<String> cmbTipoEvento;
    private JTextField txtTitulo;
    private JTextField txtUbicacion;
    private JTextField txtCupoMaximo;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    private Connection connection;

    public RegistroEventoInternalFrame() {
        super("Registro de Evento", true, true, true, true);
        setSize(800, 500);
        
        // Obtener conexión a la base de datos
        conexionDB conexion = new conexionDB();
        conexion.Connect();
        this.connection = conexion.getConnection();
        
        initComponent();
        layoutComponents();
    }

    private void initComponent() {
        txtCodigo = new JTextField(15);
        txtFecha = new JTextField(15);
        cmbTipoEvento = new JComboBox<>(new String[]{"CHARLA", "CONGRESO", "TALLER", "DEBATE"});
        txtTitulo = new JTextField(30);
        txtUbicacion = new JTextField(30);
        txtCupoMaximo = new JTextField(5);
        
        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarEvento();
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
        
        // Fila 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Código del Evento:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtCodigo, gbc);
        
        // Fila 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Fecha (dd/mm/aaaa):"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtFecha, gbc);
        
        // Fila 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Tipo de Evento:"), gbc);
        
        gbc.gridx = 1;
        panel.add(cmbTipoEvento, gbc);
        
        // Fila 3
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Título:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtTitulo, gbc);
        
        // Fila 4
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Ubicación:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtUbicacion, gbc);
        
        // Fila 5
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Cupo Máximo:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtCupoMaximo, gbc);
        
        // Fila 6 - Botones
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        panel.add(buttonPanel, gbc);
        
        setContentPane(panel);
    }

    private void guardarEvento() {
        // Validaciones
        if (txtCodigo.getText().isEmpty() || txtFecha.getText().isEmpty() || 
            txtTitulo.getText().isEmpty() || txtUbicacion.getText().isEmpty() || 
            txtCupoMaximo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Validar fecha
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            Date fecha = sdf.parse(txtFecha.getText());
            
            // Validar cupo máximo
            int cupoMaximo = Integer.parseInt(txtCupoMaximo.getText());
            if (cupoMaximo <= 0) {
                throw new NumberFormatException();
            }
            
            // Validar longitud de ubicación
            if (txtUbicacion.getText().length() > 150) {
                throw new IllegalArgumentException("La ubicación no puede tener más de 150 caracteres");
            }
            
            // Insertar en la base de datos
            String sql = "INSERT INTO EVENTO (codigo_evento, fecha_evento, tipo_evento, titulo_evento, ubicacion_evento, cupo_maximo_evento) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, txtCodigo.getText());
                pstmt.setDate(2, new java.sql.Date(fecha.getTime()));
                pstmt.setString(3, (String) cmbTipoEvento.getSelectedItem());
                pstmt.setString(4, txtTitulo.getText());
                pstmt.setString(5, txtUbicacion.getText());
                pstmt.setInt(6, cupoMaximo);
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Evento registrado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo registrar el evento", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use dd/mm/aaaa", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El cupo máximo debe ser un número entero positivo", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // Violación de clave única
                JOptionPane.showMessageDialog(this, "El código de evento ya existe", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
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
