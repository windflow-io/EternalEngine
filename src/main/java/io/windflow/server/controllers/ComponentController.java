package io.windflow.server.controllers;

import io.windflow.server.StubReader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ComponentController {

    @RequestMapping(value = "/api/components/{namespace}/{filename:^.+\\.mjs$}", produces = "text/javascript")
    @ResponseBody
    public String mjs(@PathVariable("namespace") String namespace, @PathVariable("filename") String filename) {

        String reader = StubReader.loadStub("/stubs/components/" + namespace + "/" + filename);
        return reader;
    }

}
