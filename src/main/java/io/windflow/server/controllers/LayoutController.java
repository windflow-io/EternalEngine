package io.windflow.server.controllers;

import io.windflow.server.utils.TextFileReader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class LayoutController {

    @RequestMapping(value = "/api/layouts/{namespace}/{filename:^.+\\.mjs$}", produces = "application/javascript")
    @ResponseBody
    public String mjs(@PathVariable("namespace") String namespace, @PathVariable("filename") String filename) {
        try {
            return TextFileReader.getText("/stubs/layouts/" + namespace.toLowerCase() + "/" + filename);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "{}"; /* @TODO: Need proper error handling here */
    }
}
