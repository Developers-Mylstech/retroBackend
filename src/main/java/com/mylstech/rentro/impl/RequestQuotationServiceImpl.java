package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.RequestQuotationRequest;
import com.mylstech.rentro.dto.response.RequestQuotationResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.Image;
import com.mylstech.rentro.model.Location;
import com.mylstech.rentro.model.RequestQuotation;
import com.mylstech.rentro.repository.ImageRepository;
import com.mylstech.rentro.repository.LocationRepository;
import com.mylstech.rentro.repository.RequestQuotationRepository;
import com.mylstech.rentro.service.RequestQuotationService;
import com.mylstech.rentro.util.RequestQuotationStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestQuotationServiceImpl implements RequestQuotationService {
    private static final Logger logger = LoggerFactory.getLogger(RequestQuotationServiceImpl.class);

    private final RequestQuotationRepository requestQuotationRepository;
    private final ImageRepository imageRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<RequestQuotationResponse> getAllRequestQuotations() {
        return requestQuotationRepository.findAll().stream().map(RequestQuotationResponse::new).toList();
    }

    @Override
    public RequestQuotationResponse getRequestQuotationById(Long id) {
        RequestQuotation requestQuotation = findRequestQuotationById(id);
        return new RequestQuotationResponse(requestQuotation);
    }
    
    @Override
    public RequestQuotationResponse getRequestQuotationByCode(String code) {
        RequestQuotation requestQuotation = requestQuotationRepository.findByRequestQuotationCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Request quotation not found with code: " + code));
        return new RequestQuotationResponse(requestQuotation);
    }

    @Override
    @Transactional
    public RequestQuotationResponse createRequestQuotation(RequestQuotationRequest request) {
        try {
            // Convert request to entity
            RequestQuotation requestQuotation = request.requestToRequestQuotation();
            
            // Generate a business-friendly code
            String quotationCode = generateQuotationCode();
            requestQuotation.setRequestQuotationCode(quotationCode);
            
            // Save location if provided
            if (request.getLocation() != null) {
                Location location = request.getLocation().toLocation();
                location = locationRepository.save(location);
                requestQuotation.setLocation(location);
            }
            
            // Save image if provided
            if (request.getImage() != null) {
                Image image = imageRepository.findById(request.getImage().getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + request.getImage().getImageId()));
                requestQuotation.setImage(image);
                
                // For backward compatibility
                if (requestQuotation.getProductImages() == null) {
                    requestQuotation.setProductImages(new ArrayList<>());
                }
                requestQuotation.getProductImages().add(image.getImageUrl());
            }
            
            // Save the entity
            requestQuotation = requestQuotationRepository.save(requestQuotation);
            
            logger.info("Created request quotation with ID: {} and code: {}", 
                requestQuotation.getRequestQuotationId(), requestQuotation.getRequestQuotationCode());
            return new RequestQuotationResponse(requestQuotation);
        } catch (Exception e) {
            logger.error("Error creating request quotation: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public RequestQuotationResponse updateRequestQuotation(Long id, RequestQuotationRequest request) {
        RequestQuotation existingRequestQuotation = findRequestQuotationById(id);
        
        if (request.getCompanyName() != null) {
            existingRequestQuotation.setCompanyName(request.getCompanyName());
        }
        if (request.getName() != null) {
            existingRequestQuotation.setName(request.getName());
        }
        if (request.getMobile() != null) {
            existingRequestQuotation.setMobile(request.getMobile());
        }
        
        // Update location if provided
        if (request.getLocation() != null) {
            Location location;
            if (existingRequestQuotation.getLocation() != null) {
                // Update existing location
                location = existingRequestQuotation.getLocation();
                location.setStreet(request.getLocation().getStreet());
                location.setArea(request.getLocation().getArea());
                location.setBuilding(request.getLocation().getBuilding());
                location.setVillaNo(request.getLocation().getVillaNo());
                location.setCountry(request.getLocation().getCountry());
                location.setGmapLink(request.getLocation().getGmapLink());
            } else {
                // Create new location
                location = request.getLocation().toLocation();
            }
            location = locationRepository.save(location);
            existingRequestQuotation.setLocation(location);
        }
        
        // Update image if provided
        if (request.getImage() != null) {
            Image image = imageRepository.findById(request.getImage().getImageId())
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + request.getImage().getImageId()));
            existingRequestQuotation.setImage(image);
            
            // For backward compatibility
            if (existingRequestQuotation.getProductImages() == null) {
                existingRequestQuotation.setProductImages(new ArrayList<>());
            } else {
                existingRequestQuotation.getProductImages().clear();
            }
            existingRequestQuotation.getProductImages().add(image.getImageUrl());
        }
        
        if (request.getStatus() != null) {
            existingRequestQuotation.setStatus(request.getStatus());
        }
        
        return new RequestQuotationResponse(requestQuotationRepository.save(existingRequestQuotation));
    }

    @Override
    @Transactional
    public void deleteRequestQuotation(Long id) {
        RequestQuotation requestQuotation = findRequestQuotationById(id);
        requestQuotationRepository.delete(requestQuotation);
        logger.info("Deleted request quotation with ID: {}", id);
    }
    
    @Override
    public List<RequestQuotationResponse> getRequestQuotationsByStatus(RequestQuotationStatus status) {
        return requestQuotationRepository.findByStatus(status).stream()
            .map(RequestQuotationResponse::new)
            .toList();
    }

    @Override
    public List<RequestQuotationResponse> searchRequestQuotationsByCompany(String companyName) {
        return requestQuotationRepository.findByCompanyNameContainingIgnoreCase(companyName).stream()
            .map(RequestQuotationResponse::new)
            .toList();
    }
    
    /**
     * Generate a business-friendly quotation code
     * Format: RQ-YYMM-XXXX (Year-Month-SequentialNumber)
     */
    private String generateQuotationCode() {
        // Get current year and month
        LocalDateTime now = LocalDateTime.now();
        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyMM"));
        
        // Format: RQ-YYMM-XXXX
        String codePrefix = "RQ-" + yearMonth + "-";
        
        // Find the latest code for this prefix
        List<String> latestCodes = requestQuotationRepository.findLatestCodesByPrefix(
            codePrefix, PageRequest.of(0, 1));
        
        int nextSequence = 1;
        if (!latestCodes.isEmpty()) {
            String latestCode = latestCodes.get(0);
            try {
                // Extract the sequence number from the latest code
                String sequencePart = latestCode.substring(codePrefix.length());
                nextSequence = Integer.parseInt(sequencePart) + 1;
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                // If parsing fails, start from 1
                nextSequence = 1;
            }
        }
        
        // Format the final code with the sequence padded to 4 digits
        return String.format("%s%06d", codePrefix, nextSequence);
    }
    
    /**
     * Helper method to find a request quotation by ID
     */
    private RequestQuotation findRequestQuotationById(Long id) {
        return requestQuotationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Request quotation not found with id: " + id));
    }
}
