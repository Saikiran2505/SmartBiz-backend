package com.smartbiz.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductDto {

    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private BigDecimal sellingPrice;
    private BigDecimal costPrice;
    private Integer quantity;
    private String unit;
    private Integer minStockLevel;
    private String description;
    private List<ProductFieldValueDto> customFields = new ArrayList<>();

    public ProductDto() {
    }

    public ProductDto(Long id, String name, Long categoryId, String categoryName, BigDecimal sellingPrice, BigDecimal costPrice, Integer quantity, String unit, Integer minStockLevel, String description) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.sellingPrice = sellingPrice;
        this.costPrice = costPrice;
        this.quantity = quantity;
        this.unit = unit;
        this.minStockLevel = minStockLevel;
        this.description = description;
        this.customFields = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(Integer minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProductFieldValueDto> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<ProductFieldValueDto> customFields) {
        this.customFields = customFields;
    }
}
