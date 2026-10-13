package com.smartbiz.service;

import com.smartbiz.dto.SaleDto;
import com.smartbiz.entity.Business;
import com.smartbiz.entity.Product;
import com.smartbiz.entity.Sale;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.ProductRepository;
import com.smartbiz.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;

    public SaleService(SaleRepository saleRepository,
                        ProductRepository productRepository,
                        BusinessRepository businessRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.businessRepository = businessRepository;
    }

    @Transactional
    public SaleDto recordSale(Long businessId, SaleDto dto) {
        if (dto.getProductId() == null) {
            throw new IllegalArgumentException("Product ID is required.");
        }
        if (dto.getQuantitySold() == null || dto.getQuantitySold() <= 0) {
            throw new IllegalArgumentException("Quantity sold must be greater than zero.");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));

        if (!product.getBusiness().getId().equals(businessId)) {
            throw new IllegalArgumentException("Unauthorized product access.");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found."));

        int availableStock = product.getQuantity() != null ? product.getQuantity() : 0;
        if (dto.getQuantitySold() > availableStock) {
            throw new IllegalArgumentException("Quantity sold (" + dto.getQuantitySold() + ") cannot exceed available stock (" + availableStock + ").");
        }

        // Historical price tracking: Use specified selling price or default to product's current selling price
        BigDecimal sellingPrice = (dto.getSellingPrice() != null && dto.getSellingPrice().compareTo(BigDecimal.ZERO) > 0)
                ? dto.getSellingPrice()
                : product.getSellingPrice();

        if (sellingPrice == null || sellingPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid selling price.");
        }

        // Calculate total amount = Quantity * Selling Price
        BigDecimal totalAmount = sellingPrice.multiply(BigDecimal.valueOf(dto.getQuantitySold()));

        // Reduce available stock
        product.setQuantity(availableStock - dto.getQuantitySold());
        productRepository.save(product);

        // Record Sale with Business Working Date
        Sale sale = new Sale(
                product,
                business,
                dto.getQuantitySold(),
                sellingPrice,
                totalAmount,
                business.getWorkingDate()
        );

        Sale savedSale = saleRepository.save(sale);

        return mapToDto(savedSale);
    }

    @Transactional(readOnly = true)
    public List<SaleDto> getSales(Long businessId) {
        List<Sale> sales = saleRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        return sales.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private SaleDto mapToDto(Sale sale) {
        return new SaleDto(
                sale.getId(),
                sale.getProduct().getId(),
                sale.getProduct().getName(),
                sale.getQuantitySold(),
                sale.getSellingPrice(),
                sale.getTotalAmount(),
                sale.getWorkingDate(),
                sale.getCreatedAt()
        );
    }
}
