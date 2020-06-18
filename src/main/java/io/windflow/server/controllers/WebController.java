package io.windflow.server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping(value = "/**/{regex:[-a-zA-Z0-9]*}")
    public String spa() {
        return "forward:/";
    }

}
