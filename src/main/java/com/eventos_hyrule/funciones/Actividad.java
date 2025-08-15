/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.funciones;

import java.sql.Time;
import java.time.LocalTime;

/**
 *
 * @author sofia
 */
public class Actividad {
    private String codigoActividad;
    private String codigoEvento;
    private TipoActividad tipoActividad;
    private String tituloActividad;
    private String emailEncargado;
    private Time horaInicio;
    private Time horaFin;
    private int cupoMaximo;
    
    public enum TipoActividad {
        CGARLA, TALLER, DEBATE, OTRA
    }

    public Actividad(String codigoActividad, String codigoEvento, TipoActividad tipoActividad, String tituloActividad, String emailEncargado, Time horaInicio, Time horaFin, int cupoMaximo) {
        this.codigoActividad = codigoActividad;
        this.codigoEvento = codigoEvento;
        this.tipoActividad = tipoActividad;
        this.tituloActividad = tituloActividad;
        this.emailEncargado = emailEncargado;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.cupoMaximo = cupoMaximo;
    }

    public String getCodigoActividad() {
        return codigoActividad;
    }

    public void setCodigoActividad(String codigoActividad) {
        this.codigoActividad = codigoActividad;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public TipoActividad getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(TipoActividad tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    public String getTituloActividad() {
        return tituloActividad;
    }

    public void setTituloActividad(String tituloActividad) {
        this.tituloActividad = tituloActividad;
    }

    public String getEmailEncargado() {
        return emailEncargado;
    }

    public void setEmailEncargado(String emailEncargado) {
        this.emailEncargado = emailEncargado;
    }

    public Time getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Time horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Time getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Time horaFin) {
        this.horaFin = horaFin;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }
    
}
