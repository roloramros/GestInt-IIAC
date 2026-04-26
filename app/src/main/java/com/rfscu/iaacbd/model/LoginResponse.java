package com.rfscu.iaacbd.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
}