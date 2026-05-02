package com.rfscu.iaacbd.model;

import com.google.gson.annotations.SerializedName;

public class CertificadoRequest {
    @SerializedName("no_certificado") private String noCertificado;
    @SerializedName("id_instrumento") private int idInstrumento;
    @SerializedName("estado_tecnico") private String estadoTecnico;
    private String observaciones;
    private String fecha;

    public CertificadoRequest(String noCertificado, int idInstrumento, String estadoTecnico, String observaciones, String fecha) {
        this.noCertificado = noCertificado;
        this.idInstrumento = idInstrumento;
        this.estadoTecnico = estadoTecnico;
        this.observaciones = observaciones;
        this.fecha = fecha;
    }

    // Getters and Setters (if needed)
    public String getNoCertificado() { return noCertificado; }
    public void setNoCertificado(String noCertificado) { this.noCertificado = noCertificado; }
    public int getIdInstrumento() { return idInstrumento; }
    public void setIdInstrumento(int idInstrumento) { this.idInstrumento = idInstrumento; }
    public String getEstadoTecnico() { return estadoTecnico; }
    public void setEstadoTecnico(String estadoTecnico) { this.estadoTecnico = estadoTecnico; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
