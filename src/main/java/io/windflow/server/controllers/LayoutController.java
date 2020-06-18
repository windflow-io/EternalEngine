package io.windflow.server.controllers;

import io.windflow.server.StubReader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LayoutController {

    @RequestMapping(value = "/api/layouts/{filename:^.+\\.html$}", produces = "text/html")
    @ResponseBody
    public String mjs(@PathVariable("filename") String filename) {
        String reader = StubReader.loadStub("/stubs/layouts/" + filename);
        return reader;
    }

}
