package com.qatester.hub.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@RestController
public class Task7Controller {

    private final Map<String, Long> recentRequestHashes = Collections.synchronizedMap(new HashMap<>());
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/task7")
    public String task7() {
        return "task7";
    }

    @PostMapping("/api/v2/coupon/brand/{brandId}/bet/place")
    public Map<String, Object> placeBet(
            @PathVariable String brandId,
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestBody String rawBody) {

        if (contentType == null || !contentType.contains("application/json")) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("accepted", Collections.emptyList());
            result.put("error", Collections.singletonList(Map.of("code", 100, "message", "Invalid JSON")));
            return result;
        }

        List<Map<String, Object>> bets;
        try {
            bets = objectMapper.readValue(rawBody, new TypeReference<List<Map<String, Object>>>() {});
            if (bets == null || bets.isEmpty()) {
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("accepted", Collections.emptyList());
                result.put("error", Collections.singletonList(Map.of("code", 100, "message", "Invalid request format")));
                return result;
            }
        } catch (Exception e) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("accepted", Collections.emptyList());
            result.put("error", Collections.singletonList(Map.of("code", 100, "message", "Invalid request format")));
            return result;
        }

        Map<String, Object> firstBet = bets.get(0);
        String betRequestId = firstBet.get("bet_request_id") != null 
                ? String.valueOf(firstBet.get("bet_request_id")) 
                : null;

        if (betRequestId == null || betRequestId.isEmpty()) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("accepted", Collections.emptyList());
            result.put("error", Collections.singletonList(Map.of("code", 101, "message", "Missing bet_request_id")));
            return result;
        }

        String bodyHash = sha256(rawBody);
        long currentTime = System.currentTimeMillis() / 1000;

        Long lastTime = recentRequestHashes.get(bodyHash);
        if (lastTime != null && currentTime - lastTime < 5) {
            Map<String, Object> result = new LinkedHashMap<>();
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("bet_request_id", betRequestId);
            error.put("code", 102);
            error.put("message", "You have already placed this bet");
            error.put("bet_id", "0");
            error.put("alt_stake", null);
            error.put("vip_request_available", null);
            result.put("accepted", Collections.emptyList());
            result.put("error", Collections.singletonList(error));
            return result;
        }

        recentRequestHashes.put(bodyHash, currentTime);
        String fakeBetId = String.valueOf(2594267000000000000L + random.nextInt(1000000000));

        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Object> accepted = new LinkedHashMap<>();
        accepted.put("bet_request_id", betRequestId);
        accepted.put("bet_id", fakeBetId);
        accepted.put("bonus_id", null);
        result.put("accepted", Collections.singletonList(accepted));
        result.put("error", Collections.emptyList());
        return result;
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return String.valueOf(input.hashCode());
        }
    }
}
