package com.smartbiz.service;

import com.smartbiz.dto.InventoryItemDto;
import com.smartbiz.dto.StockAdjustmentDto;
import com.smartbiz.entity.Business;
import com.smartbiz.entity.Product;
import com.smartbiz.entity.StockAdjustment;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.ProductRepository;
import com.smartbiz.repository.StockAdjustmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;

    public InventoryService(ProductRepository productRepository,
                            BusinessRepository businessRepository,
                            StockAdjustmentRepository stockAdjustmentRepository) {
        this.productRepository = productRepository;
        this.businessRepository = businessRepository;
        this.stockAdjustmentRepository = stockAdjustmentRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryItemDto> getInventory(Long businessId) {
        List<Product> products = productRepository.findByBusinessIdOrderByNameAsc(businessId);
        return products.stream().map(this::mapToInventoryItemDto).collect(Collectors.toList());
    }

    @Transactional
    public InventoryItemDto adjustStock(Long businessId, StockAdjustmentDto dto) {
        if (dto.getProductId() == null) {
            throw new IllegalArgumentException("Product ID is required.");
        }
        if (dto.getQuantityChange() == null || dto.getQuantityChange() == 0) {
            throw new IllegalArgumentException("Quantity change must be non-zero.");
        }
        if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required.");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));

        if (!product.getBusiness().getId().equals(businessId)) {
            throw new IllegalArgumentException("Unauthorized product access.");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found."));

        int currentQty = product.getQuantity() != null ? product.getQuantity() : 0;
        int newQty = currentQty + dto.getQuantityChange();

        if (newQty < 0) {
            throw new IllegalArgumentException("Stock cannot become negative. Current stock is " + currentQty + ", change requested is " + dto.getQuantityChange() + ".");
        }

        product.setQuantity(newQty);
        productRepository.save(product);

        StockAdjustment adjustment = new StockAdjustment(
                product,
                business,
                dto.getQuantityChange(),
                dto.getReason().trim(),
                business.getWorkingDate()
        );
        stockAdjustmentRepository.save(adjustment);

        return mapToInventoryItemDto(product);
    }

    @Transactional(readOnly = true)
    public List<StockAdjustmentDto> getAdjustmentHistory(Long businessId) {
        List<StockAdjustment> adjustments = stockAdjustmentRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        return adjustments.stream().map(sa -> new StockAdjustmentDto(
                sa.getId(),
                sa.getProduct().getId(),
                sa.getProduct().getName(),
                sa.getQuantityChange(),
                sa.getReason(),
                sa.getWorkingDate(),
                sa.getCreatedAt()
        )).collect(Collectors.toList());
    }

    private InventoryItemDto mapToInventoryItemDto(Product product) {
        int qty = product.getQuantity() != null ? product.getQuantity() : 0;
        int minLevel = product.getMinStockLevel() != null ? product.getMinStockLevel() : 0;

        String status;
        if (qty == 0) {
            status = "Out of Stock";
        } else if (qty <= minLevel) {
            status = "Low Stock";
        } else {
            status = "In Stock";
        }

        String catName = product.getCategory() != null ? product.getCategory().getName() : "Uncategorized";

        return new InventoryItemDto(
                product.getId(),
                product.getName(),
                catName,
                qty,
                product.getUnit() != null ? product.getUnit() : "pcs",
                minLevel,
                status
        );
    }
}
