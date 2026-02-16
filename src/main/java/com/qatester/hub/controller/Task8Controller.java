package com.qatester.hub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller for Task 8 - Microservice Integration Test
 */
@Controller
public class Task8Controller {

    private final Map<String, OrderInfo> orders = new ConcurrentHashMap<>();
    private static final long PROCESSING_DELAY_SECONDS = 1;

    private static class OrderInfo {
        String orderId;
        String productId;
        int quantity;
        String status;
        long createdAt;

        OrderInfo(String orderId, String productId, int quantity) {
            this.orderId = orderId;
            this.productId = productId;
            this.quantity = quantity;
            this.status = "PENDING";
            this.createdAt = System.currentTimeMillis() / 1000;
        }

        Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("productId", productId);
            map.put("quantity", quantity);
            map.put("status", status);
            return map;
        }
    }

    @GetMapping("/task8")
    public String task8() {
        return "task8";
    }

    @PostMapping("/api/task8")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestHeader(value = "X-Api-Action", required = false) String action,
            @RequestBody(required = false) Map<String, Object> body) {

        if (!"create_order".equals(action)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid action or method specified in headers"));
        }

        if (body == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request body must be JSON"));
        }

        if (!body.containsKey("productId") || !body.containsKey("quantity")) {
            return ResponseEntity.badRequest().body(Map.of("error", "productId and quantity are required"));
        }

        String orderId = "ord-" + (System.currentTimeMillis() / 1000);
        OrderInfo order = new OrderInfo(
                orderId,
                (String) body.get("productId"),
                ((Number) body.get("quantity")).intValue()
        );
        orders.put(orderId, order);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("status", "PENDING");
        response.put("message", "Order received. Check status in a moment.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/task8")
    public ResponseEntity<Map<String, Object>> getOrder(
            @RequestHeader(value = "X-Api-Action", required = false) String action,
            @RequestHeader(value = "X-Order-Id", required = false) String orderId) {

        if (!"get_order".equals(action)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid action or method specified in headers"));
        }

        if (orderId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "X-Order-Id header is required"));
        }

        OrderInfo order = orders.get(orderId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Order not found"));
        }

        // Update status based on time passed
        long timePassed = System.currentTimeMillis() / 1000 - order.createdAt;
        order.status = timePassed > PROCESSING_DELAY_SECONDS ? "CONFIRMED" : "PENDING";

        return ResponseEntity.ok(order.toMap());
    }
}
