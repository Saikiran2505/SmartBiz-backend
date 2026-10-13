package com.smartbiz.service;

import com.smartbiz.dto.PurchaseDto;
import com.smartbiz.entity.Business;
import com.smartbiz.entity.Product;
import com.smartbiz.entity.Purchase;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.ProductRepository;
import com.smartbiz.repository.PurchaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;

    public PurchaseService(PurchaseRepository purchaseRepository,
                            ProductRepository productRepository,
                            BusinessRepository businessRepository) {
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.businessRepository = businessRepository;
    }

    @Transactional
    public PurchaseDto recordPurchase(Long businessId, PurchaseDto dto) {
        if (dto.getProductId() == null) {
            throw new IllegalArgumentException("Product ID is required.");
        }
        if (dto.getQuantityPurchased() == null || dto.getQuantityPurchased() <= 0) {
            throw new IllegalArgumentException("Quantity purchased must be greater than zero.");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));

        if (!product.getBusiness().getId().equals(businessId)) {
            throw new IllegalArgumentException("Unauthorized product access.");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found."));

        // Use specified cost price or default to product's current cost price
        BigDecimal costPrice = (dto.getCostPrice() != null && dto.getCostPrice().compareTo(BigDecimal.ZERO) > 0)
                ? dto.getCostPrice()
                : product.getCostPrice();

        if (costPrice == null || costPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cost price must be greater than zero.");
        }

        // Total Purchase Amount = Quantity Purchased * Cost Price
        BigDecimal totalAmount = costPrice.multiply(BigDecimal.valueOf(dto.getQuantityPurchased()));

        // Inventory increases upon purchase
        int currentStock = product.getQuantity() != null ? product.getQuantity() : 0;
        product.setQuantity(currentStock + dto.getQuantityPurchased());

        // Optionally update product's current cost price
        if (dto.getUpdateProductCostPrice() == null || dto.getUpdateProductCostPrice()) {
            product.setCostPrice(costPrice);
        }

        productRepository.save(product);

        // Record Purchase with current Business Working Date
        Purchase purchase = new Purchase(
                product,
                business,
                dto.getQuantityPurchased(),
                costPrice,
                totalAmount,
                business.getWorkingDate()
        );

        Purchase savedPurchase = purchaseRepository.save(purchase);

        return mapToDto(savedPurchase);
    }

    @Transactional(readOnly = true)
    public List<PurchaseDto> getPurchases(Long businessId) {
        List<Purchase> purchases = purchaseRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        return purchases.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private PurchaseDto mapToDto(Purchase purchase) {
        return new PurchaseDto(
                purchase.getId(),
                purchase.getProduct().getId(),
                purchase.getProduct().getName(),
                purchase.getQuantityPurchased(),
                purchase.getCostPrice(),
                purchase.getTotalAmount(),
                purchase.getWorkingDate(),
                purchase.getCreatedAt()
        );
    }
}
