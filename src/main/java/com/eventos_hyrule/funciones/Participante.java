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

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String instritucion) {
        this.institucion = instritucion;
    }
    
    @Override
    public String toString(){
        return nombreCompleto + " (" + emailParticipante + ") ";
    }
    
        // Método para validar email
    public boolean emailValido() {
        return emailParticipante != null && emailParticipante.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    // Método para validar nombre
    public boolean nombreValido() {
        return nombreCompleto != null && nombreCompleto.length() <= 45 && !nombreCompleto.isEmpty();
    }
}
