package com.smartbiz.service;

import com.smartbiz.dto.WorkingDateDto;
import com.smartbiz.entity.Business;
import com.smartbiz.repository.BusinessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;

    public BusinessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    @Transactional(readOnly = true)
    public WorkingDateDto getWorkingDate(Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found for ID: " + businessId));

        return new WorkingDateDto(
                business.getWorkingDate(),
                "Current business working date fetched successfully."
        );
    }

    @Transactional
    public WorkingDateDto updateWorkingDate(Long businessId, LocalDate newWorkingDate) {
        if (newWorkingDate == null) {
            throw new IllegalArgumentException("Working date cannot be null.");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found for ID: " + businessId));

        business.setWorkingDate(newWorkingDate);
        Business updatedBusiness = businessRepository.save(business);

        return new WorkingDateDto(
                updatedBusiness.getWorkingDate(),
                "Working date updated successfully to " + updatedBusiness.getWorkingDate() + "."
        );
    }
}
