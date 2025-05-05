package com.mylstech.rentro.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

/**
 * Validator implementation for FutureDateTime annotation
 */
public class FutureDateTimeValidator implements ConstraintValidator<FutureDateTime, LocalDateTime> {
    
    private int minHours;
    
    @Override
    public void initialize(FutureDateTime constraintAnnotation) {
        this.minHours = constraintAnnotation.minHours();
    }
    
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }
        
        LocalDateTime minimumAllowedTime = LocalDateTime.now().plusHours(minHours);
        return value.isAfter(minimumAllowedTime);
    }
}