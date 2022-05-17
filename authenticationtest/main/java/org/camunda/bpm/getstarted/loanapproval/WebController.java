package com.abeam.ss.camunda.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
 
@Controller
@EnableAutoConfiguration
public class WebController {
   
    @RequestMapping("/process")
    public String process(@RequestParam(value="processInstanceId", required=true) String processInstanceId, Model model) {
        model.addAttribute("processInstanceId", processInstanceId);
	return "process";
    }
   
    @RequestMapping("/process-viewer")
    public String viewer(@RequestParam(value="processInstanceId", required=true) String processInstanceId, Model model) {
        model.addAttribute("processInstanceId", processInstanceId);
	return "process-viewer";
    }
    
}
