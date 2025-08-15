/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.funciones;

/**
 *
 * @author sofia
 */
public class ParticipanteEvento {
    private String email;
    private String nombre;
    private String tipoParticipante;
    private String metodoPago;
    private double montoPagado;

    public ParticipanteEvento(String email, String nombre, String tipoParticipante, String metodoPago, double montoPagado) {
        this.email = email;
        this.nombre = nombre;
        this.tipoParticipante = tipoParticipante;
        this.metodoPago = metodoPago;
        this.montoPagado = montoPagado;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoParticipante() {
        return tipoParticipante;
    }

    public void setTipoParticipante(String tipoParticipante) {
        this.tipoParticipante = tipoParticipante;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(double montoPagado) {
        this.montoPagado = montoPagado;
    }
    
    
}
