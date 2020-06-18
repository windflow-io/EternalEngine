package io.windflow.server.controllers;

import io.windflow.server.StubReader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PageController {

    @RequestMapping(value = "/api/pages/**/{url}", produces = "application/json")
    @ResponseBody
    public String mjs(@PathVariable("url") String filename) {
        String reader = StubReader.loadStub("/stubs/pages/index.json");
        return reader;
    }

}
