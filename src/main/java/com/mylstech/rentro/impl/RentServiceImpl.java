package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.RentRequest;
import com.mylstech.rentro.dto.response.RentResponse;
import com.mylstech.rentro.model.Rent;
import com.mylstech.rentro.repository.RentRepository;
import com.mylstech.rentro.service.RentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RentServiceImpl implements RentService {

    private final RentRepository rentRepository;

    @Value("${vat.value}")
    private Double vat;

    @Override
    public List<RentResponse> getAllRents() {
        return rentRepository.findAll().stream().map(RentResponse::new).toList();
    }

    @Override
    public RentResponse getRentById(Long id) {
        Rent rent = rentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rent not found with id: " + id));
        return new RentResponse(rent);
    }

    @Override
    public RentResponse createRent(RentRequest request) {
        Rent rent = request.requestToRent();
        rent.setVat(vat);
        return new RentResponse(rentRepository.save(rent));
    }

    @Override
    public RentResponse updateRent(Long id, RentRequest request) {
        Rent rent = rentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rent not found with id: " + id));
        rent.setMonthlyPrice(request.getMonthlyPrice());
        rent.setDiscountPrice(request.getDiscountPrice());
           return new RentResponse(rentRepository.save(rent));
    }

    @Override
    public void deleteRent(Long id) {
        Rent rent = rentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rent not found with id: " + id));
        rentRepository.delete(rent);
    }
}
