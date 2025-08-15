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
import java.util.regex.Pattern;
/**
 *
 * @author sofia
 */
public class RegistroActividadInternalFrame extends javax.swing.JInternalFrame {
   
    // Componentes UI
    private JTextField txtCodigoActividad;
    private JComboBox<String> cmbEvento;
    private JComboBox<String> cmbTipoActividad;
    private JTextField txtTitulo;
    private JComboBox<String> cmbEncargado;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;
    private JTextField txtCupoMaximo;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    private Connection connection;
/**
     * Creates new form ActividadInternalFrame
     */
    public RegistroActividadInternalFrame() {
        super("Registro de Actividad", true, true, true, true);
        setSize(650, 500);
        
        conexionDB conexion  = new conexionDB();
        conexion.Connect();
        this.connection = conexion.getConnection();
        
        initComponent();
        layoutComponents();
        cargarEventos();
        cargarEncargados();
    }
    
    
    private void initComponent() {
        txtCodigoActividad = new JTextField(15);
        cmbEvento = new JComboBox<>();
        cmbTipoActividad = new JComboBox<>(new String[]{"CHARLA", "TALLER", "DEBATE", "OTRA"});
        txtTitulo = new JTextField(30);
        cmbEncargado = new JComboBox<>();
        txtHoraInicio = new JTextField(5);
        txtHoraFin = new JTextField(5);
        txtCupoMaximo = new JTextField(5);
        
        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarActividad();
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
        
        // Fila 0 = Códgio Actividad
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Código Actividad:"),gbc);
        
        gbc.gridx = 1;
        panel.add(txtCodigoActividad, gbc);
        
        // Fila 1 = Codigo Evento
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Código Evento:"),gbc);
        
        gbc.gridx = 1;
        panel.add(cmbEvento, gbc);
        
        // Fila 2 = Tipo Actividad
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Tipo Actividad:"),gbc);
        
        gbc.gridx = 1;
        panel.add(cmbTipoActividad, gbc);
        
        // Fila 3 = Titulo Actividad
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Titulo Actividad:"),gbc);
        
        gbc.gridx = 1;
        panel.add(txtTitulo, gbc);
        
        // Fila 4 = Encargado Actividad
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Encargado Actividad:"),gbc);
        
        gbc.gridx = 1;
        panel.add(cmbEncargado, gbc);
        
        // Fila 5 = Horario
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Horario Actividad:"),gbc);
        
        gbc.gridx = 1;
        JPanel horarioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        horarioPanel.add(txtHoraInicio);
        horarioPanel.add(new JLabel(" a "));
        horarioPanel.add(txtHoraFin);
        panel.add(horarioPanel, gbc);        
        
        // Fila 6 = Cupo Máximo
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Cupo Máximo:"), gbc);
        
        gbc.gridx = 1;
        panel.add(txtCupoMaximo, gbc);
        
        // Fila 7 = Botones
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        panel.add(buttonPanel, gbc);
        
        setContentPane(panel);
    }
        
    private void cargarEventos() {
        try {
            String sql = "SELECT codigo_evento, titulo_evento FROM EVENTO ORDER BY fecha_evento";
            var stmt = connection.createStatement();
            var rs = stmt.executeQuery(sql);
            
            cmbEvento.removeAllItems();
            while (rs.next()) {
                cmbEvento.addItem(rs.getString("codigo_evento") + " - " + rs.getString("titulo_evento"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar eventos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }    
        
    private void cargarEncargados() {
        try {
            // Solo participantes de tipo CONFERENCISTA, TALLERISTA y OTRO
            String sql = "SELECT i.email_participante, p.nombre_completo " +
                         "FROM PARTICIPANTE p " +
                         "JOIN INSCRIPCION i ON p.email_participante = i.email_participante " +
                         "WHERE i.tipo_inscripcion != 'ASISTENTE'";
            var stmt = connection.createStatement();
            var rs = stmt.executeQuery(sql);
            
            cmbEncargado.removeAllItems();
            while (rs.next()) {
                cmbEncargado.addItem(rs.getString("email_participante") + " - " + rs.getString("nombre_completo"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar encargados: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    } 

    private void guardarActividad() {
        // Validaciones
        if (txtCodigoActividad.getText().isEmpty() || txtTitulo.getText().isEmpty() || 
            txtHoraInicio.getText().isEmpty() || txtHoraFin.getText().isEmpty() || 
            txtCupoMaximo.getText().isEmpty() || cmbEvento.getSelectedIndex() == -1 || 
            cmbEncargado.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar formato de código de actividad
        if (!txtCodigoActividad.getText().matches("^ACT-\\d{3}$")) {
            JOptionPane.showMessageDialog(this, "Código de actividad inválido. Debe ser ACT- seguido de 3 dígitos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar longitud del título
        if (txtTitulo.getText().length() > 200) {
            JOptionPane.showMessageDialog(this, "El título no puede exceder 200 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar formato de horas (hh:mm)
        if (!Pattern.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", txtHoraInicio.getText()) || 
            !Pattern.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", txtHoraFin.getText())) {
            JOptionPane.showMessageDialog(this, "Formato de hora inválido. Use hh:mm (24 horas)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar cupo máximo
        try {
            int cupoMaximo = Integer.parseInt(txtCupoMaximo.getText());
            if (cupoMaximo <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El cupo máximo debe ser un número entero positivo", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Extraer código de evento y email del encargado de los combos
        String eventoSeleccionado = (String) cmbEvento.getSelectedItem();
        String codigoEvento = eventoSeleccionado.split(" - ")[0];
        
        String encargadoSeleccionado = (String) cmbEncargado.getSelectedItem();
        String emailEncargado = encargadoSeleccionado.split(" - ")[0];
        
        // Insertar en la base de datos
        String sql = "INSERT INTO ACTIVIDAD (codigo_actividad, codigo_evento, tipo_actividad, titulo_actividad, " +
                     "email_encargado, hora_inicio, hora_fin, cupo_maximo_actividad) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, txtCodigoActividad.getText());
            pstmt.setString(2, codigoEvento);
            pstmt.setString(3, (String) cmbTipoActividad.getSelectedItem());
            pstmt.setString(4, txtTitulo.getText());
            pstmt.setString(5, emailEncargado);
            pstmt.setString(6, txtHoraInicio.getText());
            pstmt.setString(7, txtHoraFin.getText());
            pstmt.setInt(8, Integer.parseInt(txtCupoMaximo.getText()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Actividad registrada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar la actividad", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // Violación de clave única
                JOptionPane.showMessageDialog(this, "El código de actividad ya existe", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void limpiarCampos() {
        txtCodigoActividad.setText("");
        txtTitulo.setText("");
        txtHoraInicio.setText("");
        txtHoraFin.setText("");
        txtCupoMaximo.setText("");
        cmbTipoActividad.setSelectedIndex(0);
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
