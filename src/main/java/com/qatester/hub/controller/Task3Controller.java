package com.qatester.hub.controller;

import com.qatester.hub.graphql.GraphQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for Task 3 - GraphQL Schema Explorer
 */
@Controller
public class Task3Controller {

    @Autowired
    private GraphQLService graphQLService;

    @GetMapping("/task3")
    public String task3() {
        return "task3";
    }

    @PostMapping("/task3/graphql")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> graphql(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");
        String operationName = (String) request.get("operationName");

        Map<String, Object> result = graphQLService.execute(query, variables, operationName);
        return ResponseEntity.ok(result);
    }
}
