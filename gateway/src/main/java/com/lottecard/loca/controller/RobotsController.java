package com.lottecard.loca.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
public class RobotsController {
    @ResponseBody
    @GetMapping(value = "/robots.txt", produces = "text/plain")
    public String robots() throws IOException {
        Resource resource = new ClassPathResource("static/robots.txt");
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }
}
