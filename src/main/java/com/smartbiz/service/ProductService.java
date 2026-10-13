package com.smartbiz.service;

import com.smartbiz.dto.ProductDto;
import com.smartbiz.dto.ProductFieldValueDto;
import com.smartbiz.entity.Business;
import com.smartbiz.entity.Category;
import com.smartbiz.entity.CategoryField;
import com.smartbiz.entity.Product;
import com.smartbiz.entity.ProductFieldValue;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.CategoryFieldRepository;
import com.smartbiz.repository.CategoryRepository;
import com.smartbiz.repository.ProductFieldValueRepository;
import com.smartbiz.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BusinessRepository businessRepository;
    private final CategoryFieldRepository categoryFieldRepository;
    private final ProductFieldValueRepository productFieldValueRepository;
    private final com.smartbiz.repository.SaleRepository saleRepository;
    private final com.smartbiz.repository.PurchaseRepository purchaseRepository;
    private final com.smartbiz.repository.StockAdjustmentRepository stockAdjustmentRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          BusinessRepository businessRepository,
                          CategoryFieldRepository categoryFieldRepository,
                          ProductFieldValueRepository productFieldValueRepository,
                          com.smartbiz.repository.SaleRepository saleRepository,
                          com.smartbiz.repository.PurchaseRepository purchaseRepository,
                          com.smartbiz.repository.StockAdjustmentRepository stockAdjustmentRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.businessRepository = businessRepository;
        this.categoryFieldRepository = categoryFieldRepository;
        this.productFieldValueRepository = productFieldValueRepository;
        this.saleRepository = saleRepository;
        this.purchaseRepository = purchaseRepository;
        this.stockAdjustmentRepository = stockAdjustmentRepository;
    }

    @Transactional
    public ProductDto createProduct(Long businessId, ProductDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category selection is required.");
        }
        if (dto.getSellingPrice() == null || dto.getSellingPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valid selling price is required.");
        }
        if (dto.getCostPrice() == null || dto.getCostPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valid cost price is required.");
        }

        String name = dto.getName().trim();
        if (productRepository.existsByNameAndBusinessId(name, businessId)) {
            throw new IllegalArgumentException("Product '" + name + "' already exists.");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found."));

        Category category = categoryRepository.findByIdAndBusinessId(dto.getCategoryId(), businessId)
                .orElseThrow(() -> new IllegalArgumentException("Selected category not found or access denied."));

        Product product = new Product(
                name,
                category,
                business,
                dto.getSellingPrice(),
                dto.getCostPrice(),
                dto.getQuantity() != null ? dto.getQuantity() : 0,
                dto.getUnit(),
                dto.getMinStockLevel() != null ? dto.getMinStockLevel() : 5,
                dto.getDescription()
        );

        Product saved = productRepository.save(product);

        // Save Custom Field Values if provided
        saveCustomFieldValues(businessId, saved, dto.getCustomFields());

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProducts(Long businessId) {
        return productRepository.findByBusinessIdOrderByNameAsc(businessId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long businessId, Long id) {
        Product product = productRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found or access denied."));
        return mapToDto(product);
    }

    @Transactional
    public ProductDto updateProduct(Long businessId, Long id, ProductDto dto) {
        Product product = productRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found or access denied."));

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category selection is required.");
        }

        String newName = dto.getName().trim();
        if (!product.getName().equalsIgnoreCase(newName) &&
                productRepository.existsByNameAndBusinessId(newName, businessId)) {
            throw new IllegalArgumentException("Product name '" + newName + "' is already taken.");
        }

        Category category = categoryRepository.findByIdAndBusinessId(dto.getCategoryId(), businessId)
                .orElseThrow(() -> new IllegalArgumentException("Selected category not found or access denied."));

        product.setName(newName);
        product.setCategory(category);
        if (dto.getSellingPrice() != null) product.setSellingPrice(dto.getSellingPrice());
        if (dto.getCostPrice() != null) product.setCostPrice(dto.getCostPrice());
        if (dto.getQuantity() != null) product.setQuantity(dto.getQuantity());
        if (dto.getUnit() != null) product.setUnit(dto.getUnit());
        if (dto.getMinStockLevel() != null) product.setMinStockLevel(dto.getMinStockLevel());
        product.setDescription(dto.getDescription());

        Product updated = productRepository.save(product);

        // Save Custom Field Values
        saveCustomFieldValues(businessId, updated, dto.getCustomFields());

        return mapToDto(updated);
    }

    @Transactional
    public void deleteProduct(Long businessId, Long id) {
        Product product = productRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found or access denied."));

        long salesCount = saleRepository.countByProductId(id);
        long purchasesCount = purchaseRepository.countByProductId(id);
        if (salesCount > 0 || purchasesCount > 0) {
            throw new IllegalArgumentException("Cannot delete product because it has associated sales or purchase transaction records.");
        }

        productFieldValueRepository.deleteByProductId(id);
        stockAdjustmentRepository.deleteByProductId(id);
        productRepository.delete(product);
    }

    private void saveCustomFieldValues(Long businessId, Product product, List<ProductFieldValueDto> fieldValues) {
        if (fieldValues == null || fieldValues.isEmpty()) {
            return;
        }

        for (ProductFieldValueDto item : fieldValues) {
            if (item.getFieldId() == null) continue;

            CategoryField field = categoryFieldRepository.findByIdAndBusinessId(item.getFieldId(), businessId)
                    .orElse(null);

            if (field == null || !field.getCategory().getId().equals(product.getCategory().getId())) {
                continue;
            }

            ProductFieldValue pfv = productFieldValueRepository
                    .findByProductIdAndFieldId(product.getId(), field.getId())
                    .orElseGet(() -> new ProductFieldValue(product, field, ""));

            pfv.setFieldValue(item.getValue() != null ? item.getValue().trim() : "");
            productFieldValueRepository.save(pfv);
        }
    }

    private ProductDto mapToDto(Product p) {
        ProductDto dto = new ProductDto(
                p.getId(),
                p.getName(),
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.getSellingPrice(),
                p.getCostPrice(),
                p.getQuantity(),
                p.getUnit(),
                p.getMinStockLevel(),
                p.getDescription()
        );

        if (p.getCategory() != null) {
            List<CategoryField> fields = categoryFieldRepository.findByCategoryIdAndBusinessId(
                    p.getCategory().getId(),
                    p.getBusiness().getId()
            );

            Map<Long, String> existingValues = productFieldValueRepository.findByProductId(p.getId())
                    .stream()
                    .collect(Collectors.toMap(pfv -> pfv.getField().getId(), pfv -> pfv.getFieldValue() != null ? pfv.getFieldValue() : ""));

            List<ProductFieldValueDto> customFieldDtos = new ArrayList<>();
            for (CategoryField f : fields) {
                String val = existingValues.getOrDefault(f.getId(), "");
                customFieldDtos.add(new ProductFieldValueDto(
                        null,
                        f.getId(),
                        f.getFieldName(),
                        f.getFieldType(),
                        f.getOptions(),
                        val
                ));
            }
            dto.setCustomFields(customFieldDtos);
        }

        return dto;
    }
}
