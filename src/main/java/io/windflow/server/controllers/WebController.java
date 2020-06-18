package io.windflow.server.controllers;

import io.windflow.server.StubReader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class WebController {

    @GetMapping(value = "/**/{regex:[-a-zA-Z0-9]*}")
    public String spa() {
        return "forward:/";
    }

    @RequestMapping(value = "/api/components/{filename:^.+\\.mjs$}", produces = "text/javascript")
    @ResponseBody
    public String mjs(@PathVariable("filename") String filename) {
        String reader = StubReader.loadStub("/stubs/" + filename);
        return reader;
    }

}
