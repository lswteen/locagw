package com.lottecard.loca.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
public class CDNController {
    @ResponseBody
    @GetMapping(value = "/akamai/sureroute-test-object.html", produces = "text/html")
    public String robots() throws IOException {
        Resource resource = new ClassPathResource("static/sureroute-test-object.html");
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }
}
