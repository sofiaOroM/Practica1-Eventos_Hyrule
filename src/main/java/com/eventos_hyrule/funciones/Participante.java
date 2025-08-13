/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.funciones;

/*
 * @author sofia
 */
public class Participante {
    private String emailParticipante;
    private String nombreCompleto;
    private TipoParticipante tipoParticipante;
    private String institucion;
    
    public enum TipoParticipante{
        ESTUDIANTE, PROFESIONAL, INVITADO
    }
    
    public Participante(String emailParticipante, String nombreCompleto, TipoParticipante tipoParticipanete, String institucion){
        this.emailParticipante = emailParticipante;
        this.nombreCompleto = nombreCompleto;
        this.tipoParticipante = tipoParticipante;
        this.institucion = institucion;
    }

    public String getEmailParticipante() {
        return emailParticipante;
    }

    public void setEmailParticipante(String emailParticipante) {
        this.emailParticipante = emailParticipante;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public TipoParticipante getTipoParticipante() {
        return tipoParticipante;
    }

    public void setTipoParticipante(TipoParticipante tipoParticipante) {
        this.tipoParticipante = tipoParticipante;
    }

    public String getInstritucion() {
        return institucion;
    }

    public void setInstritucion(String instritucion) {
        this.institucion = instritucion;
    }
    
    @Override
    public String toString(){
        return nombreCompleto + " (" + emailParticipante + ") ";
    }
    
}
