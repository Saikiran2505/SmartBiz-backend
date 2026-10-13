package com.smartbiz.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_field_values")
public class ProductFieldValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private CategoryField field;

    @Column(name = "field_value")
    private String fieldValue;

    public ProductFieldValue() {
    }

    public ProductFieldValue(Product product, CategoryField field, String fieldValue) {
        this.product = product;
        this.field = field;
        this.fieldValue = fieldValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public CategoryField getField() {
        return field;
    }

    public void setField(CategoryField field) {
        this.field = field;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}
