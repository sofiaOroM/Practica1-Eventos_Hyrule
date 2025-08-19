/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.funciones;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 *
 * @author sofia
 */
public class Certificado {
    private int idCertificado;
    private String emailParticipante;
    private String codigoEvento;
    private Timestamp fechaEmision;
    private String rutaArchivo;
    private String tituloEvento;
    private Date fechaEvento;
    private String nombreEncargado;
    
    
    // Campos adicionales para visualización (no persisten en BD)
    private String nombreParticipante;

    public Date getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(Date fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    
    // Getters y Setters
    public int getIdCertificado() {
        return idCertificado;
    }
    
    public void setIdCertificado(int idCertificado) {
        this.idCertificado = idCertificado;
    }
    
    public String getEmailParticipante() {
        return emailParticipante;
    }
    
    public void setEmailParticipante(String emailParticipante) {
        this.emailParticipante = emailParticipante;
    }
    
    public String getCodigoEvento() {
        return codigoEvento;
    }
    
    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }
    
    public Timestamp getFechaEmision() {
        return fechaEmision;
    }
    
    public void setFechaEmision(Timestamp fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    
    public String getRutaArchivo() {
        return rutaArchivo;
    }
    
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
    
    public String getNombreParticipante() {
        return nombreParticipante;
    }
    
    public void setNombreParticipante(String nombreParticipante) {
        this.nombreParticipante = nombreParticipante;
    }
    
    public String getTituloEvento() {
        return tituloEvento;
    }
    
    public void setTituloEvento(String tituloEvento) {
        this.tituloEvento = tituloEvento;
    }
    
    public String getEncargado(){
        return nombreEncargado;
    }
    
    public void setOrganizador(){
        this.nombreEncargado = nombreEncargado;
    }
    // Método para obtener fecha formateada
    public String getFechaEmisionFormateada() {
        if (fechaEmision != null) {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(fechaEmision);
        }
        return "";
    }
}
