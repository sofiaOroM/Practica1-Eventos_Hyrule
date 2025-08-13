/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.eventos_hyrule;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author sofia
 */
public class Eventos_Hyrule {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        conexionDB connection = new conexionDB();
        connection.Connect();
        
                
        SwingUtilities.invokeLater(() -> {
        panelInicial app = new panelInicial();
            app.setVisible(true);
            
            // Centrar la ventana en la pantalla
            app.setLocationRelativeTo(null);
        });
    }
}
