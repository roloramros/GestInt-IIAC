package com.rfscu.iaacbd.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Instrumento implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("planta")
    private String planta;

    @SerializedName("tag")
    private String tag;

    @SerializedName("instrumento")
    private String instrumento;

    @SerializedName("tarjeta")
    private String tarjeta;

    @SerializedName("dir_im")
    private String dirIm;

    @SerializedName("dir_pa")
    private String dirPa;

    @SerializedName("var_medida")
    private String varMedida;

    @SerializedName("comunicacion")
    private String comunicacion;

    @SerializedName("seguridad")
    private String seguridad;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("low_warning")
    private Integer lowWarning;

    @SerializedName("high_warning")
    private Integer highWarning;

    @SerializedName("low_alarm")
    private Integer lowAlarm;

    @SerializedName("high_alarm")
    private Integer highAlarm;

    @SerializedName("start_wr")
    private Integer startWr;

    @SerializedName("end_wr")
    private Integer endWr;

    @SerializedName("start_mr")
    private Integer startMr;

    @SerializedName("end_mr")
    private Integer endMr;

    @SerializedName("no_serie")
    private String noSerie;

    @SerializedName("rango")
    private String rango;

    @SerializedName("user_update")
    private String userUpdate;

    public Instrumento() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlanta() { return planta; }
    public void setPlanta(String planta) { this.planta = planta; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getInstrumento() { return instrumento; }
    public void setInstrumento(String instrumento) { this.instrumento = instrumento; }

    public String getTarjeta() { return tarjeta; }
    public void setTarjeta(String tarjeta) { this.tarjeta = tarjeta; }

    public String getDirIm() { return dirIm; }
    public void setDirIm(String dirIm) { this.dirIm = dirIm; }

    public String getDirPa() { return dirPa; }
    public void setDirPa(String dirPa) { this.dirPa = dirPa; }

    public String getVarMedida() { return varMedida; }
    public void setVarMedida(String varMedida) { this.varMedida = varMedida; }

    public String getComunicacion() { return comunicacion; }
    public void setComunicacion(String comunicacion) { this.comunicacion = comunicacion; }

    public String getSeguridad() { return seguridad; }
    public void setSeguridad(String seguridad) { this.seguridad = seguridad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getLowWarning() { return lowWarning; }
    public void setLowWarning(Integer lowWarning) { this.lowWarning = lowWarning; }

    public Integer getHighWarning() { return highWarning; }
    public void setHighWarning(Integer highWarning) { this.highWarning = highWarning; }

    public Integer getLowAlarm() { return lowAlarm; }
    public void setLowAlarm(Integer lowAlarm) { this.lowAlarm = lowAlarm; }

    public Integer getHighAlarm() { return highAlarm; }
    public void setHighAlarm(Integer highAlarm) { this.highAlarm = highAlarm; }

    public Integer getStartWr() { return startWr; }
    public void setStartWr(Integer startWr) { this.startWr = startWr; }

    public Integer getEndWr() { return endWr; }
    public void setEndWr(Integer endWr) { this.endWr = endWr; }

    public Integer getStartMr() { return startMr; }
    public void setStartMr(Integer startMr) { this.startMr = startMr; }

    public Integer getEndMr() { return endMr; }
    public void setEndMr(Integer endMr) { this.endMr = endMr; }

    public String getNoSerie() { return noSerie; }
    public void setNoSerie(String noSerie) { this.noSerie = noSerie; }

    public String getRango() { return rango; }
    public void setRango(String rango) { this.rango = rango; }

    public String getUserUpdate() { return userUpdate; }
    public void setUserUpdate(String userUpdate) { this.userUpdate = userUpdate; }
}
