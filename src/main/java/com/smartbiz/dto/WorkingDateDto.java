package com.smartbiz.dto;

import java.time.LocalDate;

public class WorkingDateDto {

    private LocalDate workingDate;
    private String message;

    public WorkingDateDto() {
    }

    public WorkingDateDto(LocalDate workingDate, String message) {
        this.workingDate = workingDate;
        this.message = message;
    }

    public LocalDate getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(LocalDate workingDate) {
        this.workingDate = workingDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
