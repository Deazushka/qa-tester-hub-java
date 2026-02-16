package com.qatester.hub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.util.*;

/**
 * Controller for Task 7 - Duplicate Bet Investigation
 */
@Controller
public class Task7Controller {

    private final Map<String, Long> recentRequestHashes = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    @GetMapping("/task7")
    public String task7() {
        return "task7";
    }

    @PostMapping("/api/v2/coupon/brand/{brandId}/bet/place")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> placeBet(
            @PathVariable String brandId,
            @RequestBody String rawBody,
            @RequestHeader(value = "Content-Type", required = false) String contentType) {

        // Check if JSON
        if (contentType == null || !contentType.contains("application/json")) {
            return ResponseEntity.ok(Map.of(
                    "accepted", Collections.emptyList(),
                    "error", List.of(Map.of("code", 100, "message", "Invalid JSON"))
            ));
        }

        try {
            List<Map<String, Object>> bets = objectMapper.readValue(rawBody, List.class);
            if (bets == null || bets.isEmpty()) {
                throw new IllegalArgumentException("Expected non-empty array");
            }

            Map<String, Object> firstBet = bets.get(0);
            String betRequestId = (String) firstBet.get("bet_request_id");

            if (betRequestId == null) {
                return ResponseEntity.ok(Map.of(
                        "accepted", Collections.emptyList(),
                        "error", List.of(Map.of("code", 101, "message", "Missing bet_request_id"))
                ));
            }

            // Calculate hash for duplicate detection
            String bodyHash = sha256(rawBody);
            long currentTime = System.currentTimeMillis() / 1000;

            Long lastTime = recentRequestHashes.get(bodyHash);
            if (lastTime != null && currentTime - lastTime < 5) {
                return ResponseEntity.ok(Map.of(
                        "accepted", Collections.emptyList(),
                        "error", List.of(Map.of(
                                "bet_request_id", betRequestId,
                                "code", 102,
                                "message", "You have already placed this bet",
                                "bet_id", "0",
                                "alt_stake", null,
                                "vip_request_available", null
                        ))
                ));
            }

            recentRequestHashes.put(bodyHash, currentTime);
            long fakeBetId = 2594267000000000000L + random.nextInt(1000000000);

            return ResponseEntity.ok(Map.of(
                    "accepted", List.of(Map.of(
                            "bet_request_id", betRequestId,
                            "bet_id", String.valueOf(fakeBetId),
                            "bonus_id", null
                    )),
                    "error", Collections.emptyList()
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "accepted", Collections.emptyList(),
                    "error", List.of(Map.of("code", 100, "message", "Invalid request format"))
            ));
        }
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
