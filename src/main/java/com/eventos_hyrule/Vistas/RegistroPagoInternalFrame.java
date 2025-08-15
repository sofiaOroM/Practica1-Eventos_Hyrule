/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.eventos_hyrule.Vistas;

import com.eventos_hyrule.conexionDB;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
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
    private Map<String, Double> costosEventos = new HashMap<>();

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
        cmbEventos.addActionListener(e -> cargarCostoEvento());
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
        txtMonto.setEditable(false);

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
        panel.add(new JLabel("Evento:"), gbc);

        gbc.gridx = 1;
        panel.add(cmbEventos, gbc);

        // Fila 1 - Evento
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Participante:"), gbc);

        gbc.gridx = 1;
        panel.add(cmbParticipantes, gbc);

        // Fila 2 - Método de Pago
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Método de Pago:"), gbc);

        gbc.gridx = 1;
        panel.add(cmbMetodoPago, gbc);

        // Fila 3 - Monto
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Monto (Q):"), gbc);

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
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

                cmbParticipantes.removeAllItems();
                cmbParticipantes.addItem("-- Seleccione participante --");
                while (rs.next()) {
                    cmbParticipantes.addItem(rs.getString("email_participante") + " - " + rs.getString("nombre_completo"));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar participantes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarEventos() {
        try {
            String sql = "SELECT codigo_evento, titulo_evento, costo_evento FROM EVENTO ORDER BY fecha_evento DESC";

            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

                cmbEventos.removeAllItems();
                cmbEventos.addItem("-- Seleccione evento --");
                costosEventos.clear();

                while (rs.next()) {
                    String codigo = rs.getString("codigo_evento");
                    String titulo = rs.getString("titulo_evento");
                    double costo = rs.getDouble("costo_evento");

                    cmbEventos.addItem(titulo + " (" + codigo + ")");
                    costosEventos.put(codigo, costo);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar eventos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarCostoEvento() {
        if (cmbEventos.getSelectedIndex() > 0) {
            String seleccion = (String) cmbEventos.getSelectedItem();
            String codigoEvento = seleccion.substring(seleccion.lastIndexOf("(") + 1, seleccion.indexOf(")"));
            Double costo = costosEventos.get(codigoEvento);

            if (costo != null) {
                txtMonto.setValue(costo);
                cargarParticipantesInscritos(codigoEvento);
            }
        } else {
            txtMonto.setValue(0.0);
            cmbParticipantes.setSelectedIndex(0);
        }
    }

    private void cargarParticipantesInscritos(String codigoEvento) {
        try {
            String sql = "SELECT p.email_participante, p.nombre_completo "
                    + "FROM PARTICIPANTE p "
                    + "JOIN INSCRIPCION i ON p.email_participante = i.email_participante "
                    + "WHERE i.codigo_evento = ? "
                    + "ORDER BY p.nombre_completo";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, codigoEvento);
                ResultSet rs = pstmt.executeQuery();

                cmbParticipantes.removeAllItems();
                cmbParticipantes.addItem("-- Seleccione participante --");

                while (rs.next()) {
                    cmbParticipantes.addItem(rs.getString("nombre_completo") + " (" + rs.getString("email_participante") + ")");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar participantes inscritos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarPago() {
        // Validar selecciones
        if (cmbParticipantes.getSelectedIndex() <= 0 || cmbEventos.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un participante y un evento", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            // Extraer código de evento de forma segura
            String seleccionEvento = (String) cmbEventos.getSelectedItem();
            int inicioCodigo = seleccionEvento.lastIndexOf("(");
            int finCodigo = seleccionEvento.lastIndexOf(")");

            if (inicioCodigo == -1 || finCodigo == -1 || inicioCodigo >= finCodigo) {
                throw new IllegalArgumentException("Formato de evento inválido");
            }

            String codigoEvento = seleccionEvento.substring(inicioCodigo + 1, finCodigo);

            // Extraer email de participante de forma segura
            String seleccionParticipante = (String) cmbParticipantes.getSelectedItem();
            int inicioEmail = seleccionParticipante.lastIndexOf("(");
            int finEmail = seleccionParticipante.lastIndexOf(")");

            if (inicioEmail == -1 || finEmail == -1 || inicioEmail >= finEmail) {
                throw new IllegalArgumentException("Formato de participante inválido");
            }

            String emailParticipante = seleccionParticipante.substring(inicioEmail + 1, finEmail);
            String metodoPago = (String) cmbMetodoPago.getSelectedItem();
            double monto = ((Number) txtMonto.getValue()).doubleValue();

            // Validar monto positivo
            if (monto <= 0) {
                throw new IllegalArgumentException("El monto debe ser mayor que cero");
            }

            // Verificar inscripción
            if (!existeInscripcion(emailParticipante, codigoEvento)) {
                throw new IllegalStateException("El participante no está inscrito en este evento");
            }

            // Verificar pago existente
            if (existePago(emailParticipante, codigoEvento)) {
                throw new IllegalStateException("Ya existe un pago registrado para esta inscripción");
            }

            // Registrar transacción
            connection.setAutoCommit(false);
            try {
                // 1. Registrar pago
                registrarPagoBD(emailParticipante, codigoEvento, metodoPago, monto);

                // 2. Validar inscripción
                validarInscripcionBD(emailParticipante, codigoEvento);

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
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error en la transacción: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarPagoBD(String email, String codigoEvento, String metodo, double monto) throws SQLException {
        String sql = "INSERT INTO PAGO (email_participante, codigo_evento, metodo_pago, monto, fecha_pago) "
                + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);
            pstmt.setString(3, metodo);
            pstmt.setDouble(4, monto);

            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("No se pudo registrar el pago");
            }
        }
    }

    private void validarInscripcionBD(String email, String codigoEvento) throws SQLException {
        String sql = "UPDATE INSCRIPCION SET validada = true "
                + "WHERE email_participante = ? AND codigo_evento = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, codigoEvento);

            if (pstmt.executeUpdate() == 0) {
                throw new SQLException("No se pudo validar la inscripción");
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
