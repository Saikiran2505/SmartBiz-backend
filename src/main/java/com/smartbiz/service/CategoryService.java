package com.smartbiz.service;

import com.smartbiz.dto.CategoryDto;
import com.smartbiz.entity.Business;
import com.smartbiz.entity.Category;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.CategoryRepository;
import com.smartbiz.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           BusinessRepository businessRepository,
                           ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CategoryDto createCategory(Long businessId, CategoryDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required.");
        }

        String name = dto.getName().trim();
        if (categoryRepository.existsByNameAndBusinessId(name, businessId)) {
            throw new IllegalArgumentException("Category '" + name + "' already exists.");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found."));

        Category category = new Category(name, dto.getDescription(), business);
        Category saved = categoryRepository.save(category);

        return new CategoryDto(saved.getId(), saved.getName(), saved.getDescription());
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Long businessId) {
        return categoryRepository.findByBusinessIdOrderByNameAsc(businessId)
                .stream()
                .map(c -> new CategoryDto(c.getId(), c.getName(), c.getDescription()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDto updateCategory(Long businessId, Long id, CategoryDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required.");
        }

        Category category = categoryRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found or access denied."));

        String newName = dto.getName().trim();
        if (!category.getName().equalsIgnoreCase(newName) &&
                categoryRepository.existsByNameAndBusinessId(newName, businessId)) {
            throw new IllegalArgumentException("Category name '" + newName + "' is already taken.");
        }

        category.setName(newName);
        category.setDescription(dto.getDescription());

        Category updated = categoryRepository.save(category);
        return new CategoryDto(updated.getId(), updated.getName(), updated.getDescription());
    }

    @Transactional
    public void deleteCategory(Long businessId, Long id) {
        Category category = categoryRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found or access denied."));

        long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw new IllegalStateException("Cannot delete category because it contains " + productCount + " associated products.");
        }

        categoryRepository.delete(category);
    }
}
