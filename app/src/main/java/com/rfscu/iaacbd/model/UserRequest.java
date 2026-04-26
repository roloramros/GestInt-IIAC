package com.rfscu.iaacbd.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserRequest {
    
    @SerializedName("user_name")
    private String username;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("rol")
    private String role;
    
    public UserRequest() {}
    
    public UserRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
