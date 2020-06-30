package io.windflow.server.controllers;

import io.windflow.server.StubReader;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.UncheckedIOException;

@RestController
public class PageController {

    @RequestMapping(value = {"/api/pages/{host}/{urlPath}", "/api/pages/{host}"}, produces = "application/json")
    @ResponseBody
    public String mjs(@PathVariable("host") String host, @PathVariable(value = "urlPath", required = false) String urlPath, HttpServletResponse response) {
        System.out.println(urlPath);
        urlPath = (urlPath == null ? "/index" : "/" + urlPath);
        host = !host.contains(":") ? host : host.substring(0, host.indexOf(":"));
        String filePath = "/stubs/pages/" + host + urlPath + ".json";

        System.out.println(filePath);

        try {
            return StubReader.loadStub(filePath);
        } catch (UncheckedIOException ex) {
            System.err.println("WINDFLOW ERROR: " + ex.getMessage());
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return StubReader.loadStub("/stubs/pages/windflowx/404.json");
        }
    }
}

