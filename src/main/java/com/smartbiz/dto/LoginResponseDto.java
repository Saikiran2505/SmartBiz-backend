package com.smartbiz.dto;

public class LoginResponseDto {

    private String status;
    private String token;
    private String tokenType = "Bearer";
    private Long ownerId;
    private String username;
    private Long businessId;
    private String businessName;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String status, String token, Long ownerId, String username, Long businessId, String businessName) {
        this.status = status;
        this.token = token;
        this.tokenType = "Bearer";
        this.ownerId = ownerId;
        this.username = username;
        this.businessId = businessId;
        this.businessName = businessName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
