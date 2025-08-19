/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.eventos_hyrule.Vistas;

import com.eventos_hyrule.funciones.Certificado;
import com.eventos_hyrule.Controlador.CertificadoControlador;
import com.eventos_hyrule.conexionDB;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author sofia
 */
public class CertificadosInternalFrame extends javax.swing.JInternalFrame {
  
    private final CertificadoControlador certificadoService;
    private final JDesktopPane desktopPane;
    
    // Componentes de la interfaz
    private JTextField txtEmail;
    private JComboBox<String> cmbEventos;
    private JButton btnGenerar;
    private JButton btnVer;
    private JButton btnBuscar;
    private JTable tblCertificados;
    private DefaultTableModel modeloTabla;
    
    public CertificadosInternalFrame(CertificadoControlador certificadoService, JDesktopPane desktopPane) {
        this.certificadoService = certificadoService;
        this.desktopPane = desktopPane;
        
        initComponent();
        configurarVentana();
        cargarEventos();
    }
    
    private void initComponent() {
        // Panel superior con controles de búsqueda
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        panelSuperior.add(new JLabel("Email Participante:"));
        txtEmail = new JTextField(20);
        panelSuperior.add(txtEmail);
        
        panelSuperior.add(new JLabel("Evento:"));
        cmbEventos = new JComboBox<>();
        cmbEventos.setPreferredSize(new Dimension(150, 25));
        panelSuperior.add(cmbEventos);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(this::buscarCertificados);
        panelSuperior.add(btnBuscar);
        
        btnGenerar = new JButton("Generar Certificado");
        btnGenerar.addActionListener(this::generarCertificado);
        panelSuperior.add(btnGenerar);
        
        btnVer = new JButton("Ver Certificado");
        btnVer.addActionListener(this::verCertificado);
        panelSuperior.add(btnVer);
        
        // Configuración de la tabla de resultados
        modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Participante", "Evento", "Fecha Emisión", "Archivo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };
        
        tblCertificados = new JTable(modeloTabla);
        tblCertificados.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tblCertificados.getColumnModel().getColumn(1).setPreferredWidth(150); // Participante
        tblCertificados.getColumnModel().getColumn(2).setPreferredWidth(100); // Evento
        tblCertificados.getColumnModel().getColumn(3).setPreferredWidth(120); // Fecha
        tblCertificados.getColumnModel().getColumn(4).setPreferredWidth(200); // Archivo
        
        JScrollPane scrollPane = new JScrollPane(tblCertificados);
        
        // Configuración del layout principal
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelSuperior, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }
    
    private void configurarVentana() {
        setTitle("Gestión de Certificados");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
    }
    
    private void cargarEventos() {
        cmbEventos.removeAllItems();
        cmbEventos.addItem("Cargando eventos...");
        cmbEventos.setEnabled(false);
        new Thread(() -> {
            try {
                List<String> eventos = certificadoService.obtenerEventosConCertificados();
                SwingUtilities.invokeLater(() -> {
                    cmbEventos.removeAllItems();
                
                    if (eventos.isEmpty()) {
                        cmbEventos.addItem("No hay eventos disponibles");
                    } else {
                        cmbEventos.addItem("Todos los eventos");
                        eventos.forEach(cmbEventos::addItem);
                        cmbEventos.setEnabled(true);
                    }
                /*cmbEventos.removeAllItems();
                    cmbEventos.addItem("Todos los eventos");
                    for (String evento : eventos) {
                        cmbEventos.addItem(evento);
                    }*/
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    cmbEventos.removeAllItems();
                    cmbEventos.addItem("Error al cargar eventos");
                    mostrarError("Detalles del error: " + e.getMessage());
                });
                e.printStackTrace();          //      mostrarError("Error al cargar eventos: " + e.getMessage());
            }
        }).start();
    }
    
    private void buscarCertificados(ActionEvent evt) {
        new Thread(() -> {
            try {
                String email = txtEmail.getText().trim();
                String evento = cmbEventos.getSelectedIndex() == 0 ? 
                    null : cmbEventos.getSelectedItem().toString();
                
                List<Certificado> certificados = certificadoService.buscarCertificados(email, evento);
                
                SwingUtilities.invokeLater(() -> {
                    modeloTabla.setRowCount(0); // Limpiar tabla
                    
                    for (Certificado cert : certificados) {
                        modeloTabla.addRow(new Object[]{
                            cert.getIdCertificado(),
                            cert.getNombreParticipante() + " (" + cert.getEmailParticipante() + ")",
                            cert.getCodigoEvento(),
                            cert.getFechaEmisionFormateada(),
                            cert.getRutaArchivo()
                        });
                    }
                });
            } catch (Exception e) {
                mostrarError("Error al buscar certificados: " + e.getMessage());
            }
        }).start();
    }
    
    private void generarCertificado(ActionEvent evt) {
        new Thread(() -> {
            try {
                String email = txtEmail.getText().trim();
                if (email.isEmpty()) {
                    mostrarAdvertencia("Debe ingresar un email de participante");
                    return;
                }
                
                if (cmbEventos.getSelectedIndex() == 0) {
                    mostrarAdvertencia("Debe seleccionar un evento específico");
                    return;
                }
                
                String codigoEvento = cmbEventos.getSelectedItem().toString();
                
                // Mostrar confirmación
                int confirm = JOptionPane.showConfirmDialog(
                    CertificadosInternalFrame.this,
                    "¿Generar certificado para " + email + " en el evento " + codigoEvento + "?",
                    "Confirmar generación",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    Certificado certificado = certificadoService.generarCertificado(email, codigoEvento);
                    
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            CertificadosInternalFrame.this,
                            "Certificado generado exitosamente:\n" +
                            "Archivo: " + certificado.getRutaArchivo(),
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Actualizar la búsqueda
                        buscarCertificados(null);
                    });
                }
            } catch (Exception e) {
                mostrarError("Error al generar certificado: " + e.getMessage());
            }
        }).start();
    }
    
    private void verCertificado(ActionEvent evt) {
        int filaSeleccionada = tblCertificados.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarAdvertencia("Debe seleccionar un certificado de la tabla");
            return;
        }
        
        new Thread(() -> {
            try {
                int idCertificado = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
                Certificado certificado = certificadoService.obtenerCertificado(idCertificado);
                
                SwingUtilities.invokeLater(() -> {
                    // Abrir ventana de visualización
                    CertificadoViewerInternalFrame viewer = new CertificadoViewerInternalFrame(certificado);
                    viewer.setVisible(true);
                    desktopPane.add(viewer);
                    viewer.toFront();
                });
            } catch (Exception e) {
                mostrarError("Error al visualizar certificado: " + e.getMessage());
            }
        }).start();
    }
    
    // Métodos auxiliares para mostrar mensajes
    private void mostrarError(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    private void mostrarAdvertencia(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
        });
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
