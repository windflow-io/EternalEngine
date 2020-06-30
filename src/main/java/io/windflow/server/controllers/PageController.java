package io.windflow.server.controllers;

import io.windflow.server.StubReader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UncheckedIOException;

@RestController
public class PageController {

    @RequestMapping(value = {"/api/pages/**"}, produces = "application/json")
    @ResponseBody
    public String servePage(HttpServletRequest request, HttpServletResponse response) {

        RequestParser parser = new RequestParser(request);

        try {
            return StubReader.loadStub(parser.getFilePath());
        } catch (UncheckedIOException ex) {
            System.err.println("WINDFLOW ERROR: " + ex.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return StubReader.loadStub("/stubs/pages/windflowx/404.json");
        }
    }

    private static class RequestParser {

        private HttpServletRequest request;
        private String filePath;

        RequestParser(HttpServletRequest request) {
            String requestedPath = request.getRequestURI().replace("/api/pages", "");
            String hostAndPort = requestedPath.split("/")[1];
            String urlPath = requestedPath.replace("/" + hostAndPort + "/", "");
            urlPath = urlPath.endsWith("/") ? urlPath.substring(0, urlPath.length() -1) : urlPath;
            String host = !hostAndPort.contains(":") ? hostAndPort : hostAndPort.substring(0, hostAndPort.indexOf(":"));
            urlPath = (urlPath.length() == 0 ? "/index" : "/" + urlPath);
            this.filePath = "/stubs/pages/" + host + urlPath + ".json";
        }

        public String getFilePath() {
            return this.filePath;
        }

    }
}
