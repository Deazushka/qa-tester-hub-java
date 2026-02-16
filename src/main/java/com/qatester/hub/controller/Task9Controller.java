package com.qatester.hub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for Task 9 - Frontend/Backend Mismatch
 */
@Controller
public class Task9Controller {

    @GetMapping("/task9")
    public String task9() {
        return "task9";
    }

    @PostMapping("/api/task9")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerUser(
            @RequestBody(required = false) Map<String, Object> body) {

        if (body == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid request: request body must be JSON and not empty."
            ));
        }

        if (!body.containsKey("username")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid data: 'username' is a required field."
            ));
        }

        String username = (String) body.get("username");
        return ResponseEntity.ok(Map.of(
                "message", "User '" + username + "' successfully registered."
        ));
    }
}
