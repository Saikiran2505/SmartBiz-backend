package com.smartbiz.dto;

public class RegisterResponseDto {

    private String status;
    private String message;
    private Long ownerId;
    private Long businessId;
    private String username;
    private String businessName;

    public RegisterResponseDto() {
    }

    public RegisterResponseDto(String status, String message, Long ownerId, Long businessId, String username, String businessName) {
        this.status = status;
        this.message = message;
        this.ownerId = ownerId;
        this.businessId = businessId;
        this.username = username;
        this.businessName = businessName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
