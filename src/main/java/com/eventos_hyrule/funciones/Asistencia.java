/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.funciones;

import java.sql.Timestamp;

/**
 *
 * @author sofia
 */
public class Asistencia {
    private String emailParticipante;
    private String codigoActividad;
    private Timestamp fechaAsistencia;

    public Asistencia(String emailParticipante, String codigoActividad, Timestamp fechaAsistencia) {
        this.emailParticipante = emailParticipante;
        this.codigoActividad = codigoActividad;
        this.fechaAsistencia = fechaAsistencia;
    }

    public String getEmailParticipante() {
        return emailParticipante;
    }

    public String getCodigoActividad() {
        return codigoActividad;
    }

    public Timestamp getFechaAsistencia() {
        return fechaAsistencia;
    }
    
    
    
}
