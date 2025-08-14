/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.eventos_hyrule.Vistas;

import com.eventos_hyrule.Controlador.AsistenciaControlador;
import com.eventos_hyrule.Controlador.ParticipanteControlador;
import com.eventos_hyrule.Controlador.ActividadControlador;
import com.eventos_hyrule.funciones.Asistencia;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
/**
 *
 * @author sofia
 */
public class RegistroAsistenciaInternalFrame extends javax.swing.JInternalFrame {
    private JComboBox<String> cmbParticipantes;
    private JComboBox<String> cmbActividades;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    
    private Connection connection;
    private AsistenciaControlador asistenciaControlador;
    private ParticipanteControlador participanteControlador;
    private ActividadControlador actividadControlador;
    /**
     * Creates new form RegistroAsistenciaInternalFrame
     */
    public RegistroAsistenciaInternalFrame() {
        super("Registro de Asistencia", true, true, true, true);
        setSize(500, 300);
        
        this.connection = connection;
        this.asistenciaControlador = new AsistenciaControlador(connection);
        this.participanteControlador = new ParticipanteControlador(connection);
        this.actividadControlador = new ActividadControlador(connection);
                
        initComponent();
        layoutComponents();
        cargarParticipantes();
        cargarActividades();        
    }
private void initComponent() {
        cmbParticipantes = new JComboBox<>();
        cmbActividades = new JComboBox<>();
        
        btnRegistrar = new JButton("Registrar Asistencia");
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarAsistencia();
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
        
        // Fila 0 - Participante
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Participante:"), gbc);
        
        gbc.gridx = 1;
        panel.add(cmbParticipantes, gbc);
        
        // Fila 1 - Actividad
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Actividad:"), gbc);
        
        gbc.gridx = 1;
        panel.add(cmbActividades, gbc);
        
        // Fila 2 - Botones
        gbc.gridx = 0;
        gbc.gridy = 2;
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
            participanteControlador.listarParticipantes().forEach(participante -> {
                cmbParticipantes.addItem(participante.getNombreCompleto() + " (" + participante.getEmail() + ")");
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar participantes: " + e.getMessage(), 
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarActividades() {
        try {
            actividadControlador.listarActividades().forEach(actividad -> {
                cmbActividades.addItem(actividad.getTitulo() + " (" + actividad.getCodigoActividad() + ")");
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar actividades: " + e.getMessage(), 
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarAsistencia() {
        if (cmbParticipantes.getSelectedIndex() == -1 || cmbActividades.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un participante y una actividad", 
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Extraer email del participante (está entre paréntesis en el combo)
            String participanteStr = (String) cmbParticipantes.getSelectedItem();
            String emailParticipante = participanteStr.substring(participanteStr.indexOf("(") + 1, participanteStr.indexOf(")"));
            
            // Extraer código de actividad (está entre paréntesis en el combo)
            String actividadStr = (String) cmbActividades.getSelectedItem();
            String codigoActividad = actividadStr.substring(actividadStr.indexOf("(") + 1, actividadStr.indexOf(")"));
            
            Asistencia asistencia = new Asistencia(emailParticipante, codigoActividad);
            
            if (asistenciaControlador.registrarAsistencia(asistencia)) {
                JOptionPane.showMessageDialog(this, "Asistencia registrada exitosamente", 
                                              "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "El participante ya estaba registrado en esta actividad", 
                                              "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar asistencia: " + e.getMessage(), 
                                          "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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
