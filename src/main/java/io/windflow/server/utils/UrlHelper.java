package io.windflow.server.utils;

import javax.servlet.http.HttpServletRequest;

public class UrlHelper {

    private HttpServletRequest request;
    private String domain;
    private String path;


    public UrlHelper(HttpServletRequest request) {
        String requestedPath = request.getRequestURI().replace("/api/pages", "").toLowerCase();
        String hostAndPort = requestedPath.split("/")[1];
        String urlPath = requestedPath.replace("/" + hostAndPort + "/", "");
        urlPath = urlPath.endsWith("/") ? urlPath.substring(0, urlPath.length() -1) : urlPath;
        String host = !hostAndPort.contains(":") ? hostAndPort : hostAndPort.substring(0, hostAndPort.indexOf(":"));
        this.domain = host.startsWith("www.") ? host.replace("www.", "") : host;
        this.path = (urlPath.length() == 0 ? "/" : "/" + urlPath);
    }

    public String getDomain() {
        return this.domain;
    }

    public String getPath() {
        return this.path;
    }
}
