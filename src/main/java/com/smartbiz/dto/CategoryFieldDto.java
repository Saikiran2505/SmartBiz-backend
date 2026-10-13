package com.smartbiz.dto;

public class CategoryFieldDto {

    private Long id;
    private Long categoryId;
    private String fieldName;
    private String fieldType; // TEXT, NUMBER, DATE, DROPDOWN
    private String options;   // e.g. "S,M,L,XL"

    public CategoryFieldDto() {
    }

    public CategoryFieldDto(Long id, Long categoryId, String fieldName, String fieldType, String options) {
        this.id = id;
        this.categoryId = categoryId;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.options = options;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
