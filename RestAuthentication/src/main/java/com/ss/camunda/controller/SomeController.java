package com.ss.camunda.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SomeController {
    @GetMapping("hello")
    @ResponseBody
    public String Admin(){

        return "Hello World";
    }
}