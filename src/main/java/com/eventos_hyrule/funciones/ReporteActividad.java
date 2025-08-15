/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.funciones;

import java.sql.Time;

/**
 *
 * @author sofia
 */
public class ReporteActividad {

    private String codigoActividad;
    private String codigoEvento;
    private String titulo;
    private String nombreEncargado;
    private Time horaInicio;
    private Time horaFin;
    private int cupoMaximo;
    private int participantesAsistencia;    

    public ReporteActividad(String codigoActividad, String codigoEvento, String titulo, String nombreEncargado, Time horaInicio, Time horaFin, int cupoMaximo, int participantesAsistencia) {
        this.codigoActividad = codigoActividad;
        this.codigoEvento = codigoEvento;
        this.titulo = titulo;
        this.nombreEncargado = nombreEncargado;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.cupoMaximo = cupoMaximo;
        this.participantesAsistencia = participantesAsistencia;
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNombreEncargado() {
        return nombreEncargado;
    }

    public void setNombreEncargado(String nombreEncargado) {
        this.nombreEncargado = nombreEncargado;
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

    public int getParticipantesAsistencia() {
        return participantesAsistencia;
    }

    public void setParticipantesAsistencia(int participantesAsistencia) {
        this.participantesAsistencia = participantesAsistencia;
    }
    
    
}
