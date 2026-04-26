package com.rfscu.iaacbd.model;

import com.google.gson.annotations.SerializedName;

public class InstrumentoRequest {
    @SerializedName("planta")
    private String planta;
    
    @SerializedName("tag")
    private String tag;
    
    @SerializedName("instrumento")
    private String instrumento;
    
    @SerializedName("tarjeta")
    private String tarjeta;

    public InstrumentoRequest(String tag, String planta, String instrumento, String tarjeta) {
        this.tag = tag;
        this.planta = planta;
        this.instrumento = instrumento;
        this.tarjeta = tarjeta;
    }

    public String getPlanta() {
        return planta;
    }

    public void setPlanta(String planta) {
        this.planta = planta;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(String instrumento) {
        this.instrumento = instrumento;
    }

    public String getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }
}
