/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eventos_hyrule.funciones;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sofia
 */
public class ReporteEvento {
    private String codigoEvento;
    private Date fecha;
    private String titulo;
    private String tipoEvento;
    private String ubicacion;
    private int cupoMaximo;
    private double montoTotal;
    private int participantesValidados;
    private int participantesNoValidados;
    private List<ParticipanteEvento> participantes;

    public ReporteEvento(String codigoEvento, Date fecha, String titulo, String tipoEvento, String ubicacion, int cupoMaximo,
                        double montoTotal, int participantesValidados, int participantesNoValidados){
        this.codigoEvento = codigoEvento;
        this.fecha = fecha;
        this.titulo = titulo;
        this.tipoEvento = tipoEvento;
        this.ubicacion = ubicacion;
        this.cupoMaximo = cupoMaximo;
        this.montoTotal = montoTotal;
        this.participantesValidados = participantesValidados;
        this.participantesNoValidados = participantesNoValidados;
        this.participantes = new ArrayList<>();
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
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

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public int getParticipantesValidados() {
        return participantesValidados;
    }

    public void setParticipantesValidados(int participantesValidados) {
        this.participantesValidados = participantesValidados;
    }

    public int getParticipantesNoValidados() {
        return participantesNoValidados;
    }

    public void setParticipantesNoValidados(int participantesNoValidados) {
        this.participantesNoValidados = participantesNoValidados;
    }

    public List<ParticipanteEvento> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<ParticipanteEvento> participantes) {
        this.participantes = participantes;
    }
    
    
}
