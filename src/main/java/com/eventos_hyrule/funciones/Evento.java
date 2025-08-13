/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.funciones;

import java.sql.Date;

/**
 *
 * @author sofia
 */
public class Evento {
    private String codigoEvento;
    private Date fechaEvento;
    private TipoEvento tipoEvento;
    private String tituloEvento;
    private String ubicacion;
    private int cupoMaximo;
    
    public enum TipoEvento { 
        CHARLA, CONGRESO, TALLER, DEBATE
    }
    
    public Evento(String codigoEvento, Date fechaEvento, TipoEvento tipoEvento, String tituloEvento, 
            String ubicacion, int cupoMaximo){
        this.codigoEvento = codigoEvento;
        this.fechaEvento = fechaEvento;
        this.tipoEvento = tipoEvento;
        this.tituloEvento = tituloEvento;
        this.ubicacion = ubicacion;
        this.cupoMaximo = cupoMaximo;        
        
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public Date getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(Date fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getTituloEvento() {
        return tituloEvento;
    }

    public void setTituloEvento(String tituloEvento) {
        this.tituloEvento = tituloEvento;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }
    
    @Override
    public String toString(){
        return tituloEvento + " (" + codigoEvento + ") ";
    }
}
