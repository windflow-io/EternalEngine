package io.windflow.server.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HttpsConfig implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("CHECKING");
        String requestedPort = request.getHeader("X-Forwarded-Port");
        System.out.println(requestedPort);
        if (requestedPort != null && requestedPort.equals("80")) {
            response.sendRedirect("https://" + request.getServerName() + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            return false;
        }
        return true;
    }

}
