package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.OurServiceRequest;
import com.mylstech.rentro.dto.response.OurServiceResponse;
import com.mylstech.rentro.service.OurServiceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/our-services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OurServiceController {

    private final OurServiceService ourServiceService;
    private final Logger logger = LoggerFactory.getLogger(OurServiceController.class);

    @GetMapping
    public ResponseEntity<List<OurServiceResponse>> getAllOurServices() {
        try {
            logger.debug("Fetching all our services");
            List<OurServiceResponse> ourServices = ourServiceService.getAllOurServices();
            logger.debug("Found {} our services", ourServices.size());
            return ResponseEntity.ok(ourServices);
        } catch (Exception e) {
            logger.error("Error fetching all our services", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OurServiceResponse> getOurServiceById(@PathVariable Long id) {
        try {
            logger.debug("Fetching our service with id: {}", id);
            OurServiceResponse ourService = ourServiceService.getOurServiceById(id);
            logger.debug("Found our service: {}", ourService);
            return ResponseEntity.ok(ourService);
        } catch (Exception e) {
            logger.error("Error fetching our service with id: " + id, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<OurServiceResponse> createOurService(@RequestBody OurServiceRequest request) {
        try {
            logger.debug("Creating new our service: {}", request);
            OurServiceResponse ourService = ourServiceService.createOurService(request);
            logger.debug("Created our service: {}", ourService);
            return new ResponseEntity<>(ourService, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating our service", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OurServiceResponse> updateOurService(@PathVariable Long id, @RequestBody OurServiceRequest request) {
        try {
            logger.debug("Updating our service with id: {}", id);
            OurServiceResponse ourService = ourServiceService.updateOurService(id, request);
            logger.debug("Updated our service: {}", ourService);
            return ResponseEntity.ok(ourService);
        } catch (Exception e) {
            logger.error("Error updating our service with id: " + id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOurService(@PathVariable Long id) {
        try {
            logger.debug("Deleting our service with id: {}", id);
            ourServiceService.deleteOurService(id);
            logger.debug("Deleted our service with id: {}", id);
            return ResponseEntity.ok("Our service deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting our service with id: " + id, e);
            throw e;
        }
    }
}
