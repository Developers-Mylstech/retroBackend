package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.response.OrderResponse;
import com.mylstech.rentro.service.OrderService;
import com.mylstech.rentro.util.ORDER_STATUS;
import com.mylstech.rentro.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {
    private final OrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final SecurityUtils securityUtils;

    @Operation(summary = "Get all orders", description = "Retrieve a list of all orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved orders")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        try {
            logger.debug("Fetching all orders");
            List<OrderResponse> orders = orderService.getAllOrders();
            logger.debug("Found {} orders", orders.size());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching all orders", e);
            throw e;
        }
    }

    @Operation(summary = "Get order by ID", description = "Retrieve an order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved order"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        try {
            logger.debug("Fetching order with ID: {}", id);
            OrderResponse order = orderService.getOrderById(id);
            logger.debug("Found order with ID: {}", id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error fetching order with ID: " + id, e);
            throw e;
        }
    }

    @Operation(summary = "Get orders by user ID", description = "Retrieve orders for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved orders")
    })
    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId() {

        try {
            Long userId = securityUtils.getCurrentUser ( ).getUserId ( );
            logger.debug("Fetching orders for user with ID: {}", userId);
            List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
            logger.debug("Found {} orders for user with ID: {}", orders.size(), userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {

            throw e;
        }
    }

    @Operation(summary = "Get orders by status", description = "Retrieve orders with a specific status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved orders")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable ORDER_STATUS status) {
        try {
            logger.debug("Fetching orders with status: {}", status);
            List<OrderResponse> orders = orderService.getOrdersByStatus(status);
            logger.debug("Found {} orders with status: {}", orders.size(), status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching orders with status: " + status, e);
            throw e;
        }
    }

    @Operation(summary = "Update order status", description = "Update the status of an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated order status"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam ORDER_STATUS status) {
        try {
            logger.debug("Updating status of order with ID: {} to {}", id, status);
            OrderResponse order = orderService.updateOrderStatus(id, status);
            logger.debug("Updated status of order with ID: {} to {}", id, status);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error updating status of order with ID: " + id, e);
            throw e;
        }
    }

    @Operation(summary = "Mark order as paid", description = "Mark an order as paid with a payment ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully marked order as paid"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}/pay")
    public ResponseEntity<OrderResponse> markOrderAsPaid(
            @PathVariable Long id,
            @RequestParam String paymentId) {
        try {
            logger.debug("Marking order with ID: {} as paid with payment ID: {}", id, paymentId);
            OrderResponse order = orderService.markOrderAsPaid(id, paymentId);
            logger.debug("Marked order with ID: {} as paid", id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error marking order with ID: " + id + " as paid", e);
            throw e;
        }
    }
}