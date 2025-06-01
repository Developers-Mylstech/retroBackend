package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.BannerRequest;
import com.mylstech.rentro.dto.response.BannerResponse;
import com.mylstech.rentro.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<BannerResponse> createBanner(@RequestBody BannerRequest bannerRequest) {
        BannerResponse clientResponse = bannerService.createBanner ( bannerRequest );
        return new ResponseEntity<> ( clientResponse, HttpStatus.OK );
    }

    @GetMapping("/{bannerId}")
    public ResponseEntity<BannerResponse> findByBannerId(@PathVariable Long bannerId) {
        return new ResponseEntity<> ( bannerService.getBannerById ( bannerId ), HttpStatus.OK );
    }

    @GetMapping
    public ResponseEntity<List<BannerResponse>> getAllBanner() {
        return new ResponseEntity<> ( bannerService.getAllBanners ( ), HttpStatus.OK );
    }

    @PutMapping("/{bannerId}")
    public ResponseEntity<BannerResponse> updateBannerDetails(@PathVariable Long bannerId, @RequestBody BannerRequest bannerRequest) {
        return new ResponseEntity<> ( bannerService.updateBanner ( bannerId, bannerRequest ), HttpStatus.OK );
    }

    @DeleteMapping("/{bannerId}")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long bannerId) {
        bannerService.deleteBanner ( bannerId );
        return ResponseEntity.noContent ( ).build ( );
    }

    @PutMapping("/{id}/image/{imageId}")
    @Operation(summary = "Set banner image", description = "Sets the image for a banner")
    public ResponseEntity<BannerResponse> setBannerImage(
            @PathVariable Long id,
            @PathVariable Long imageId) {
        try {
            BannerResponse banner = bannerService.setBannerImage ( id, imageId );
            return ResponseEntity.ok ( banner );
        }
        catch ( Exception e ) {
            return ResponseEntity.status ( HttpStatus.INTERNAL_SERVER_ERROR ).build ( );
        }
    }

    @DeleteMapping("/{id}/image")
    @Operation(summary = "Remove banner image", description = "Removes the image from a banner")
    public ResponseEntity<BannerResponse> removeBannerImage(@PathVariable Long id) {
        try {
            BannerResponse banner = bannerService.removeBannerImage ( id );
            return ResponseEntity.ok ( banner );
        }
        catch ( Exception e ) {
            return ResponseEntity.status ( HttpStatus.INTERNAL_SERVER_ERROR ).build ( );
        }
    }
}
