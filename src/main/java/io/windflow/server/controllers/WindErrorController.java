package io.windflow.server.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WindErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public void doError() {
        System.out.println("ERROR");
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}
