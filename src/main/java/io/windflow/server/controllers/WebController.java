package io.windflow.server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebController {

    @RequestMapping(value = "/api/**")
    @ResponseBody
    public String xxx() {
        return "API Call";
    }

    @RequestMapping(value = "/{[path:[^\\.]*}")
    public String redirect() {
        return "forward:/";
    }

}
