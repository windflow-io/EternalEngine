package io.windflow.server.controllers;

import io.windflow.server.StubReader;
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
            try {
                if (StubReader.checkDirectory(parser.getSiteRoot())) {
                    System.err.println("WINDFLOW 404 ERROR: Web page does not exist: " + ex.getMessage());
                    return StubReader.loadStub("/stubs/pages/windflowx/PageNotFound.json");
                } else {
                    System.err.println("WINDFLOW 404 ERROR: No site at this domain: " + ex.getMessage());
                    return StubReader.loadStub("/stubs/pages/windflowx/DomainNotFound.json");
                }
            } catch (UncheckedIOException ex2) {
                System.err.println("WINDFLOW 404 ERROR: Could not find the default 404 pages");
                ex2.printStackTrace();
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

                /**@TODO: We should make the client do something with this **/
                return  "    {" +
                        "         \"metaData\": {\n" +
                        "             \"title\": \"Windflow.io Error\",\n" +
                        "             \"description\":\"Default error page not found: " + ex2.getMessage() +"\",\n" +
                        "             \"httpStatus\": \"404\"\n" +
                        "         }\n" +
                        "     }";
            }
        }
    }

    private static class RequestParser {

        private HttpServletRequest request;
        private String filePath;
        private String siteRoot;

        RequestParser(HttpServletRequest request) {
            String requestedPath = request.getRequestURI().replace("/api/pages", "").toLowerCase();
            String hostAndPort = requestedPath.split("/")[1];
            String urlPath = requestedPath.replace("/" + hostAndPort + "/", "");
            urlPath = urlPath.endsWith("/") ? urlPath.substring(0, urlPath.length() -1) : urlPath;
            String host = !hostAndPort.contains(":") ? hostAndPort : hostAndPort.substring(0, hostAndPort.indexOf(":"));
            host = host.startsWith("www.") ? host.replace("www.", "") : host;
            urlPath = (urlPath.length() == 0 ? "/index" : "/" + urlPath);
            this.filePath = "/stubs/pages/" + host + urlPath + ".json";
            this.siteRoot = "/stubs/pages/" + host;
        }

        public String getFilePath() {
            return this.filePath;
        }

        public String getSiteRoot() {
            return this.siteRoot;
        }

    }
}
