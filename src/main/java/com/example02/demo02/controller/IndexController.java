package com.example02.demo02.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 映射/index  到
 */
@Controller
@RequestMapping
public class IndexController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}