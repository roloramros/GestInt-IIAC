package com.rfscu.iaacbd.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String id;
    
    @SerializedName("user_name")
    private String username;
    
    @SerializedName("rol")
    private String role;
    
    @SerializedName("created_at")
    private String createdAt;
    
    public User() {}
    
    public User(String id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
