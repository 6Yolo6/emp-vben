package com.ldjt.emp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloController TODO description
 *
 * @author wdf
 * @since 2025/9/26 9:03
 */
@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping("/api/health")
    public String health() {
        return "Application is running!";
    }
}
