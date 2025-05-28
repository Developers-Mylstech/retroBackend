package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.CheckOutRequest;
import com.mylstech.rentro.dto.response.CheckOutResponse;
import com.mylstech.rentro.service.CheckOutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/checkouts")
@RequiredArgsConstructor
@Tag(name = "Checkout", description = "Checkout management APIs")
public class CheckOutController {
    private static final Logger logger = LoggerFactory.getLogger ( CheckOutController.class );

    private final CheckOutService checkOutService;

    @Operation(summary = "Get all checkouts", description = "Retrieve a list of all checkouts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved checkouts")
    })
    @GetMapping
    public ResponseEntity<List<CheckOutResponse>> getAllCheckOuts() {

            logger.debug ( "Fetching all checkouts" );
            List<CheckOutResponse> checkOuts = checkOutService.getAllCheckOuts ( );
            logger.debug ( "Found {} checkouts", checkOuts.size ( ) );
            return ResponseEntity.ok ( checkOuts );

    }

    @Operation(summary = "Get checkout by ID", description = "Retrieve a checkout by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved checkout"),
            @ApiResponse(responseCode = "404", description = "Checkout not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CheckOutResponse> getCheckOutById(@PathVariable Long id) {

            logger.debug ( "Fetching checkout with ID: {}", id );
            CheckOutResponse checkOut = checkOutService.getCheckOutById ( id );
            logger.debug ( "Found checkout with ID: {}", id );
            return ResponseEntity.ok ( checkOut );

    }

    @Operation(summary = "Create checkout", description = "Create a new checkout. Delivery date must be at least 1 hour in the future.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Checkout created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters or delivery date not in the future"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @PostMapping
    public ResponseEntity<CheckOutResponse> createCheckOut(@Valid @RequestBody CheckOutRequest request) {

            logger.info ( "Creating new checkout: {}", request );
            CheckOutResponse checkOut = checkOutService.createCheckOut ( request );
            logger.info ( "Created checkout with ID: {}", checkOut.getCheckoutId ( ) );
            return new ResponseEntity<> ( checkOut, HttpStatus.CREATED );

    }

    @Operation(summary = "Update checkout", description = "Update an existing checkout. If delivery date is provided, it must be at least 1 hour in the future.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters or delivery date not in the future"),
            @ApiResponse(responseCode = "404", description = "Checkout not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CheckOutResponse> updateCheckOut(
            @PathVariable Long id,
            @Valid @RequestBody CheckOutRequest request) {

            logger.debug ( "Updating checkout with ID: {}", id );
            CheckOutResponse checkOut = checkOutService.updateCheckOut ( id, request );
            logger.debug ( "Updated checkout with ID: {}", id );
            return ResponseEntity.ok ( checkOut );

    }

    @Operation(summary = "Delete checkout", description = "Delete a checkout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Checkout deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Checkout not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCheckOut(@PathVariable Long id) {

            logger.debug ( "Deleting checkout with ID: {}", id );
            checkOutService.deleteCheckOut ( id );
            logger.debug ( "Deleted checkout with ID: {}", id );
            return ResponseEntity.noContent ( ).build ( );
    }

    @Operation(summary = "Get checkouts by user ID", description = "Retrieve checkouts for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved checkouts")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CheckOutResponse>> getCheckOutsByUserId(@PathVariable Long userId) {

            logger.debug ( "Fetching checkouts for user with ID: {}", userId );
            List<CheckOutResponse> checkOuts = checkOutService.getCheckOutsByUserId ( userId );
            logger.debug ( "Found {} checkouts for user with ID: {}", checkOuts.size ( ), userId );
            return ResponseEntity.ok ( checkOuts );
    }


    @Operation(summary = "Place order", description = "Place an order from a checkoutID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully placed order"),
            @ApiResponse(responseCode = "404", description = "Checkout not found")
    })
    @PostMapping("/{id}/place-order")
    public ResponseEntity<CheckOutResponse> placeOrder(@PathVariable("id") Long checkoutId) {

            logger.debug ( "Placing order for checkout with ID: {}", checkoutId );
            CheckOutResponse response = checkOutService.placeOrder ( checkoutId );
            logger.debug ( "Placed order for checkout with ID: {}", checkoutId );
            return ResponseEntity.ok ( response );

    }
}
