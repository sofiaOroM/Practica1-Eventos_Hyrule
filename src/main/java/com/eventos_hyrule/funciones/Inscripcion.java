/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.funciones;

/**
 *
 * @author sofia
 */
public class Inscripcion {
    private String emailParticipante;
    private String codigoEvento;
    private TipoInscripcion tipoInscripcion;
    private Boolean validada;
    
    public enum TipoInscripcion{
        ASISTENTE, CONFERENCISTA,TALLERISTA, OTRO
    }

    public Inscripcion(String emailParticipante, String codigoEvento, TipoInscripcion tipoInscripcion, Boolean validada) {
        this.emailParticipante = emailParticipante;
        this.codigoEvento = codigoEvento;
        this.tipoInscripcion = tipoInscripcion;
        this.validada = validada;
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

    public TipoInscripcion getTipoInscripcion() {
        return tipoInscripcion;
    }

    public void setTipoInscripcion(TipoInscripcion tipoInscripcion) {
        this.tipoInscripcion = tipoInscripcion;
    }

    public Boolean getValidada() {
        return validada;
    }

    public void setValidada(Boolean validada) {
        this.validada = validada;
    }
}
