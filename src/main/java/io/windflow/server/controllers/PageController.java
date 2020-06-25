package io.windflow.server.controllers;

import antlr.StringUtils;
import io.windflow.server.StubReader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PageController {

    @RequestMapping(value = {"/api/pages/{host}/{url}", "/api/pages/{host}"}, produces = "application/json")
    @ResponseBody
    public String mjs( @PathVariable("host") String host, @PathVariable(value = "url", required = false) String url) {
        url = url == null ? "/" : "/" + url;
        switch (url) {
            case "/": return StubReader.loadStub("/stubs/pages/index.json");
            case "/about": return StubReader.loadStub("/stubs/pages/about.json");
            case "/contact": return StubReader.loadStub("/stubs/pages/contact.json");
            default:
                return StubReader.loadStub("/stubs/pages/404.json");
        }
    }

}
