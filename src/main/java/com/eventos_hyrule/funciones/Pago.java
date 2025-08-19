/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.funciones;

import java.math.BigDecimal;
import java.sql.Date;

/**
 *
 * @author sofia
 */
public class Pago {
    private String emailParticipante;
    private String codigoEvento;
    private String metodoPago;
    private BigDecimal monto;
    private Date fecha;

    public enum MetodoPago{
        EFECTIVO, TRANSFERENCIA,TARJETA        
    }
    public Pago(String emailParticipante, String codigoEvento, String metodoPago, BigDecimal monto, Date fecha) {
        this.emailParticipante = emailParticipante;
        this.codigoEvento = codigoEvento;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.fecha = fecha;
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

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
