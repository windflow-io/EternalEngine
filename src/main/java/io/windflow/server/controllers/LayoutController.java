package io.windflow.server.controllers;

import io.windflow.server.StubReader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LayoutController {

    @RequestMapping(value = "/api/layouts/{namespace}/{filename:^.+\\.mjs$}", produces = "application/javascript")
    @ResponseBody
    public String mjs(@PathVariable("namespace") String namespace, @PathVariable("filename") String filename) {
        return StubReader.loadStub("/stubs/layouts/" + namespace.toLowerCase() + "/" + filename);
    }
}
