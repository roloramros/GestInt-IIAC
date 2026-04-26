package com.rfscu.iaacbd.model;

public class LoginRequest {
    private String user_name;
    private String password;

    public LoginRequest(String user_name, String password) {
        this.user_name = user_name;
        this.password = password;
    }
}