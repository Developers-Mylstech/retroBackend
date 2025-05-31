package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.RentRequest;
import com.mylstech.rentro.dto.response.RentResponse;
import com.mylstech.rentro.model.Rent;
import com.mylstech.rentro.repository.RentRepository;
import com.mylstech.rentro.service.RentService;
import com.mylstech.rentro.util.UNIT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RentServiceImpl implements RentService {

    private static final String RENT_NOT_FOUND_WITH_ID = "Rent not found with id: ";
    private final RentRepository rentRepository;

    @Value("${vat.value}")
    private Double vat;

    @Override
    public List<RentResponse> getAllRents() {
        return rentRepository.findAll ( ).stream ( ).map ( RentResponse::new ).toList ( );
    }

    @Override
    public RentResponse getRentById(Long id) {
        Rent rent = rentRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( RENT_NOT_FOUND_WITH_ID + id ) );
        return new RentResponse ( rent );
    }

    @Override
    public RentResponse createRent(RentRequest request) {
        Rent rent = request.requestToRent ( );
        return new RentResponse ( rentRepository.save ( rent ) );
    }

    @Override
    public RentResponse updateRent(Long id, RentRequest request) {
        Rent rent = rentRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( RENT_NOT_FOUND_WITH_ID + id ) );
        if ( request.getMonthlyPrice ( ) != null ) {
            rent.setMonthlyPrice ( request.getMonthlyPrice ( ) );
        }
        if ( request.getDiscountUnit ( ) == UNIT.AED ) {
            rent.setDiscountPrice ( rent.getMonthlyPrice ( ) - request.getDiscountValue ( ) );
        } else if ( request.getDiscountUnit ( ) == UNIT.PERCENTAGE ) {
            rent.setDiscountPrice ( rent.getMonthlyPrice ( ) -
                    (rent.getMonthlyPrice ( ) * (request.getDiscountValue ( )
                            / 100)) );
        }
        if ( Boolean.TRUE.equals ( request.getIsVatIncluded ( ) ) ) {
            rent.setVat ( vat );
            rent.setDiscountPrice ( rent.getMonthlyPrice ( ) -
                    (rent.getMonthlyPrice ( ) * (rent.getVat ( )
                            / 100)) );
        } else if ( Boolean.FALSE.equals ( request.getIsVatIncluded ( ) ) ) {
            rent.setVat ( 0.0 );
        }
        if ( request.getDiscountValue ( ) != null ) {
            rent.setDiscountValue ( request.getDiscountValue ( ) );
        }

        if ( request.getBenefits ( ) != null ) {
            rent.setBenefits ( request.getBenefits ( ) );
        }
        return new RentResponse ( rentRepository.save ( rent ) );
    }

    @Override
    public void deleteRent(Long id) {
        Rent rent = rentRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( RENT_NOT_FOUND_WITH_ID + id ) );
        rentRepository.delete ( rent );
    }
}
