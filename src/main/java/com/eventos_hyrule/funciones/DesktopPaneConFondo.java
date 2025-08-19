/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.funciones;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

/**
 *
 * @author sofia
 */
public class DesktopPaneConFondo extends JDesktopPane {
    private Image imagenFondo;

    public DesktopPaneConFondo(String rutaImagen) {
        try {
            imagenFondo = ImageIO.read(new File(rutaImagen));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el fondo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            imagenFondo = null;;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
}