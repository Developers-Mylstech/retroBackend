package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.BannerRequest;
import com.mylstech.rentro.dto.request.ClientRequest;
import com.mylstech.rentro.dto.response.BannerResponse;
import com.mylstech.rentro.dto.response.ClientResponse;
import com.mylstech.rentro.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/banners")
public class BannerController {
    private final BannerService bannerService;
    @PostMapping
    private ResponseEntity<BannerResponse> createBanner(@RequestBody BannerRequest bannerRequest) {
        BannerResponse clientResponse = bannerService.createBanner ( bannerRequest );
        return new ResponseEntity<> ( clientResponse, HttpStatus.OK );
    }

    @GetMapping("/{bannerId}")
    private ResponseEntity<BannerResponse> findByBannerId(@PathVariable Long bannerId){
        return new ResponseEntity<> ( bannerService.getBannerById ( bannerId ),HttpStatus.OK);
    }

    @GetMapping
    private ResponseEntity<List<BannerResponse>> getAllBanner(){
        return new ResponseEntity<> ( bannerService.getAllBanners (),HttpStatus.OK);
    }

    @PutMapping("/{bannerId}")
    private ResponseEntity<BannerResponse> updateBannerDetails(@PathVariable Long bannerId,@RequestBody BannerRequest bannerRequest){
        return new ResponseEntity<> ( bannerService.updateBanner ( bannerId,bannerRequest ) ,HttpStatus.OK);
    }
    @DeleteMapping("/{bannerId}")
    private ResponseEntity<Void> deleteBanner(@PathVariable Long bannerId){
        bannerService.deleteBanner ( bannerId );
        return ResponseEntity.noContent ( ).build ( );
    }


}
