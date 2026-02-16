package com.qatester.hub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Main controller for serving HTML pages
 */
@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/task0")
    public String task0() {
        return "task0";
    }

    @GetMapping("/task1")
    public String task1() {
        return "task1";
    }

    @GetMapping("/task2")
    public String task2() {
        return "task2";
    }

    @GetMapping("/task5")
    public String task5() {
        return "task5";
    }

    @GetMapping("/task6")
    public String task6() {
        return "task6";
    }
}
