package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.SellRequest;
import com.mylstech.rentro.dto.response.SellResponse;
import com.mylstech.rentro.model.Sell;
import com.mylstech.rentro.repository.SellRepository;
import com.mylstech.rentro.service.SellService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellServiceImpl implements SellService {

    private final SellRepository sellRepository;
    @Value ( "${vat.value}" )
    private Double vat;

    @Override
    public List<SellResponse> getAllSells() {
        return sellRepository.findAll().stream().map(SellResponse::new).toList();
    }

    @Override
    public SellResponse getSellById(Long id) {
        Sell sell = sellRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sell not found with id: " + id));
        return new SellResponse(sell);
    }

    @Override
    public SellResponse createSell(SellRequest request) {
        Sell sell = request.requestToSell ( );
        sell.setVat ( vat );
        return new SellResponse(sellRepository.save(sell));
    }

    @Override
    public SellResponse updateSell(Long id, SellRequest request) {
        Sell sell = sellRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sell not found with id: " + id));
        sell.setActualPrice(request.getActualPrice());
        sell.setDiscountPrice(request.getDiscountPrice());

        return new SellResponse(sellRepository.save(sell));
    }

    @Override
    public void deleteSell(Long id) {
        Sell sell = sellRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sell not found with id: " + id));
        sellRepository.delete(sell);
    }
}
