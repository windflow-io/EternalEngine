package io.windflow.server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping(value = "/**/{regex:[-a-zA-Z0-9]*}")
    public String spa(Model model) {
        model.addAttribute("title", "Hello World");
        return "index";
    }

}
