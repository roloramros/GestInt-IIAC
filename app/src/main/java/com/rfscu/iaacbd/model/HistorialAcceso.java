package com.rfscu.iaacbd.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class HistorialAcceso implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("username")
    private String username;

    @SerializedName("ip_address")
    private String ipAddress;

    @SerializedName("login_time")
    private String loginTime;

    @SerializedName("status")
    private String status;

    public HistorialAcceso() {}

    public int getId() { return id; }
    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getIpAddress() { return ipAddress; }
    public String getLoginTime() { return loginTime; }
    public String getStatus() { return status; }
}