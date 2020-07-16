package io.windflow.server.controllers;

import io.windflow.server.experiment.JavaScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @RequestMapping("/test")
    @ResponseBody
    public String doTest(@Autowired JavaScript javaScript, @RequestParam("number") String number) {
        Integer result = javaScript.testJavaScript(Integer.parseInt(number));
        return number + " + 1 = " + result + " says Javascript";
    }

}
