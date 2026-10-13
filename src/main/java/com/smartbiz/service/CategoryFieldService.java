package com.smartbiz.service;

import com.smartbiz.dto.CategoryFieldDto;
import com.smartbiz.entity.Business;
import com.smartbiz.entity.Category;
import com.smartbiz.entity.CategoryField;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.CategoryFieldRepository;
import com.smartbiz.repository.CategoryRepository;
import com.smartbiz.repository.ProductFieldValueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryFieldService {

    private final CategoryFieldRepository categoryFieldRepository;
    private final CategoryRepository categoryRepository;
    private final BusinessRepository businessRepository;
    private final ProductFieldValueRepository productFieldValueRepository;

    public CategoryFieldService(CategoryFieldRepository categoryFieldRepository,
                                CategoryRepository categoryRepository,
                                BusinessRepository businessRepository,
                                ProductFieldValueRepository productFieldValueRepository) {
        this.categoryFieldRepository = categoryFieldRepository;
        this.categoryRepository = categoryRepository;
        this.businessRepository = businessRepository;
        this.productFieldValueRepository = productFieldValueRepository;
    }

    @Transactional
    public CategoryFieldDto addFieldToCategory(Long businessId, Long categoryId, CategoryFieldDto dto) {
        if (dto.getFieldName() == null || dto.getFieldName().trim().isEmpty()) {
            throw new IllegalArgumentException("Field name is required.");
        }
        if (dto.getFieldType() == null || dto.getFieldType().trim().isEmpty()) {
            throw new IllegalArgumentException("Field type is required.");
        }

        String fieldName = dto.getFieldName().trim();
        String fieldType = dto.getFieldType().trim().toUpperCase();

        if (!List.of("TEXT", "NUMBER", "DATE", "DROPDOWN").contains(fieldType)) {
            throw new IllegalArgumentException("Invalid field type: " + fieldType + ". Supported types are TEXT, NUMBER, DATE, DROPDOWN.");
        }

        Category category = categoryRepository.findByIdAndBusinessId(categoryId, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found or access denied."));

        if (categoryFieldRepository.existsByFieldNameAndCategoryIdAndBusinessId(fieldName, categoryId, businessId)) {
            throw new IllegalArgumentException("Custom field '" + fieldName + "' already exists for category '" + category.getName() + "'.");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found."));

        CategoryField field = new CategoryField(fieldName, fieldType, dto.getOptions(), category, business);
        CategoryField saved = categoryFieldRepository.save(field);

        return new CategoryFieldDto(saved.getId(), categoryId, saved.getFieldName(), saved.getFieldType(), saved.getOptions());
    }

    @Transactional(readOnly = true)
    public List<CategoryFieldDto> getFieldsForCategory(Long businessId, Long categoryId) {
        return categoryFieldRepository.findByCategoryIdAndBusinessId(categoryId, businessId)
                .stream()
                .map(f -> new CategoryFieldDto(f.getId(), categoryId, f.getFieldName(), f.getFieldType(), f.getOptions()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryFieldDto updateField(Long businessId, Long categoryId, Long fieldId, CategoryFieldDto dto) {
        if (dto.getFieldName() == null || dto.getFieldName().trim().isEmpty()) {
            throw new IllegalArgumentException("Field name is required.");
        }
        if (dto.getFieldType() == null || dto.getFieldType().trim().isEmpty()) {
            throw new IllegalArgumentException("Field type is required.");
        }

        String fieldName = dto.getFieldName().trim();
        String fieldType = dto.getFieldType().trim().toUpperCase();

        if (!List.of("TEXT", "NUMBER", "DATE", "DROPDOWN").contains(fieldType)) {
            throw new IllegalArgumentException("Invalid field type: " + fieldType + ". Supported types are TEXT, NUMBER, DATE, DROPDOWN.");
        }

        CategoryField field = categoryFieldRepository.findByIdAndBusinessId(fieldId, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Category field not found or access denied."));

        if (!field.getCategory().getId().equals(categoryId)) {
            throw new IllegalArgumentException("Field does not belong to specified category.");
        }

        if (!field.getFieldName().equalsIgnoreCase(fieldName) &&
                categoryFieldRepository.existsByFieldNameAndCategoryIdAndBusinessId(fieldName, categoryId, businessId)) {
            throw new IllegalArgumentException("Custom field '" + fieldName + "' already exists for category.");
        }

        field.setFieldName(fieldName);
        field.setFieldType(fieldType);
        field.setOptions(fieldType.equals("DROPDOWN") ? (dto.getOptions() != null ? dto.getOptions().trim() : "") : null);

        CategoryField updated = categoryFieldRepository.save(field);
        return new CategoryFieldDto(updated.getId(), categoryId, updated.getFieldName(), updated.getFieldType(), updated.getOptions());
    }

    @Transactional
    public void deleteField(Long businessId, Long categoryId, Long fieldId) {
        CategoryField field = categoryFieldRepository.findByIdAndBusinessId(fieldId, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Category field not found or access denied."));

        if (!field.getCategory().getId().equals(categoryId)) {
            throw new IllegalArgumentException("Field does not belong to specified category.");
        }

        productFieldValueRepository.deleteByFieldId(fieldId);
        categoryFieldRepository.delete(field);
    }
}
