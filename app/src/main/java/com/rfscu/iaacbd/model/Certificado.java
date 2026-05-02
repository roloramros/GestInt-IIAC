package com.rfscu.iaacbd.model;

import com.google.gson.annotations.SerializedName;

public class Certificado {
    private int id;
    @SerializedName("no_certificado") private String noCertificado;
    @SerializedName("estado_tecnico") private String estadoTecnico;
    private String observaciones;
    private String fecha;
    private String tag;
    @SerializedName("no_serie") private String noSerie;
    private String instrumento;
    private String descripcion;
    private String rango;

    // Getters
    public int getId() { return id; }
    public String getNoCertificado() { return noCertificado; }
    public String getEstadoTecnico() { return estadoTecnico; }
    public String getObservaciones() { return observaciones; }
    public String getFecha() { return fecha; }
    public String getTag() { return tag; }
    public String getNoSerie() { return noSerie; }
    public String getInstrumento() { return instrumento; }
    public String getDescripcion() { return descripcion; }
    public String getRango() { return rango; }
}
