package io.windflow.eternalengine.interceptors;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HttpsConfig implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestedPort = request.getHeader("X-Forwarded-Port");
        if (requestedPort != null && requestedPort.equals("80")) {
            response.sendRedirect("https://" + request.getServerName() + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            return false;
        }
        return true;
    }

}
