/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.Vistas;

import com.eventos_hyrule.funciones.Certificado;
import com.eventos_hyrule.funciones.Evento;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
/**
 *
 * @author sofia
 */
class CertificadoViewerInternalFrame extends JInternalFrame{
    private final Certificado certificado;
    
    public CertificadoViewerInternalFrame(Certificado certificado) {
        this.certificado = certificado;
        initComponents();
        configurarVentana();
    }
    
    private void initComponents() {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setText(generarHtmlCertificado());
        
        JScrollPane scrollPane = new JScrollPane(editorPane);
        
        // Botón para abrir en navegador
        JButton btnAbrirNavegador = new JButton("Abrir en Navegador");
        btnAbrirNavegador.addActionListener(e -> abrirEnNavegador());
        
        JPanel panelInferior = new JPanel();
        panelInferior.add(btnAbrirNavegador);
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(panelInferior, BorderLayout.SOUTH);
    }
    
    private void configurarVentana() {
        setTitle("Certificado: " + certificado.getCodigoEvento() + " - " + certificado.getEmailParticipante());
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
    }
    private String generarHtmlCertificado() {
    return "<!DOCTYPE html>" +
           "<html lang='es'>" +
           "<head>" +
           "<meta charset='UTF-8'>" +
           "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
           "<title>Certificado de Participación</title>" +
           "<style>" +
           "body { margin: 0; padding: 0; font-family: 'Montserrat', Arial, sans-serif; background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%); display: flex; justify-content: center; align-items: center; min-height: 100vh; }" +
           ".certificado { width: 800px; background: white; border-radius: 15px; box-shadow: 0 20px 40px rgba(0,0,0,0.15); position: relative; overflow: hidden; border: 1px solid #e0e0e0; padding: 40px; }" +
           ".certificado-border { position: absolute; width: 96%; height: 94%; border: 2px dashed #3a7bd5; border-radius: 10px; top: 2%; left: 2%; z-index: 1; opacity: 0.3; }" +
           ".watermark { position: absolute; opacity: 0.05; font-size: 180px; font-weight: 900; color: #3a7bd5; top: 50%; left: 50%; transform: translate(-50%, -50%); z-index: 0; user-select: none; }" +
           ".titulo { color: #3a7bd5; font-size: 36px; font-weight: 700; margin-bottom: 10px; letter-spacing: 2px; text-align: center; }" +
           ".subtitulo { color: #666; font-size: 18px; text-align: center; margin-bottom: 30px; }" +
           ".nombre { font-size: 32px; font-weight: 700; color: #222; text-align: center; margin: 30px 0; text-transform: uppercase; letter-spacing: 1px; }" +
           ".email { font-size: 12px; color: #555; text-align: center; margin-bottom: 20px; }" +
           ".evento { font-size: 28px; font-weight: 600; color: #333; text-align: center; margin: 20px 0; padding: 15px; background: linear-gradient(90deg, rgba(58,123,213,0.1) 0%, rgba(58,123,213,0.05) 100%); border-left: 4px solid #3a7bd5; }" +
           ".fecha-evento { font-size: 16px; color: #555; text-align: center; margin-bottom: 30px; }" +
           ".detalles { display: flex; justify-content: space-around; margin: 30px 0; }" +
           ".detalle { text-align: center; }" +
           ".detalle-label { font-size: 14px; color: #777; margin-bottom: 5px; }" +
           ".detalle-valor { font-size: 16px; font-weight: 600; color: #444; }" +
           ".firma { position: absolute; right: 40px; bottom: 40px; text-align: center; }" +
           ".firma-nombre { font-weight: 600; margin-top: 10px; color: #333; }" +
           ".firma-cargo { font-size: 12px; color: #777; margin-top: 5px; }" +
           ".codigo { position: absolute; bottom: 15px; left: 30px; font-size: 12px; color: #999; }" +
           "</style>" +
           "</head>" +
           "<body>" +
           "<div class='certificado'>" +
           "<div class='certificado-border'></div>" +
           "<div class='watermark'>CERTIFICADO</div>" +
           "<div class='titulo'>CERTIFICADO DE PARTICIPACIÓN</div>" +
           "<div class='subtitulo'>Reino de Hyrule tiene el honor de otorgar a: </div>" +
           "<div class='nombre'>" + certificado.getNombreParticipante() + "</div>" +
           "<div class='email'>Correo electrónico: <strong>" + certificado.getEmailParticipante() + "</strong></div>" +
           "<div class='subtitulo'>Por su notable participacion en el evento " + certificado.getCodigoEvento() + " titulado: </div>" +
           "<div class='evento'>" + certificado.getTituloEvento() + "</div>" +
           "<div class='detalles'>" +
           "<div class='detalle'>" +
           "<div class='detalle-label'>Fecha de emisión</div>" +
           "<div class='detalle-valor'>" + certificado.getFechaEmisionFormateada() + "</div>" +
           "</div>" +
           "<div class='detalle'>" +
           "<div class='detalle-label'>Código de verificación</div>" +
           "<div class='detalle-valor'> CE-" + certificado.getIdCertificado() + "</div>" +
           "</div>" +
           "</div>" +
           "<div class='codigo'>ID: " + certificado.getIdCertificado() + "</div>" +
           "</div>" +
           "</body>" +
           "</html>";
}
    
    private void abrirEnNavegador() {
        try {
            // Crear archivo temporal
            Path tempFile = Files.createTempFile("certificado_" + certificado.getIdCertificado(), ".html");
            Files.write(tempFile, generarHtmlCertificado().getBytes());
            
            // Abrir en navegador predeterminado
            Desktop.getDesktop().browse(tempFile.toUri());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al abrir en navegador: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
