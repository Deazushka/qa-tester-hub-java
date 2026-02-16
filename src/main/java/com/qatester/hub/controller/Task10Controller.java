package com.qatester.hub.controller;

import com.qatester.hub.graphql.Task10GraphQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for Task 10 - GraphQL Missing Variable
 */
@Controller
public class Task10Controller {

    @Autowired
    private Task10GraphQLService graphQLService;

    @GetMapping("/task10")
    public String task10() {
        return "task10";
    }

    @PostMapping("/api/task10/graphql")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> graphql(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");
        String operationName = (String) request.get("operationName");

        Map<String, Object> result = graphQLService.execute(query, variables, operationName);

        if (result.containsKey("errors")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Bad Request. Invalid data provided to the GraphQL endpoint."
            ));
        }

        return ResponseEntity.ok(result);
    }
}
