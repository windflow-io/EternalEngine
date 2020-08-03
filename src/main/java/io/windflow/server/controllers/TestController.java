package io.windflow.server.controllers;

import io.windflow.server.TextFileReader;
import io.windflow.server.experiment.JavaScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
public class TestController {

    @RequestMapping(value = "/create404", produces = "application/json")
    @ResponseBody
    public String create404(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        System.err.println("Simulating an error 404");
        return "{\"error\":\"404\"}";
    }

    @RequestMapping(value = "/create500", produces = "application/json")
    @ResponseBody
    public String create500(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        System.err.println("Simulating an error 500");
        return "{\"error\":\"500\"}";
    }

    @RequestMapping(value = "/createServerError", produces = "application/json")
    @ResponseBody
    public String createServerError(HttpServletResponse response) {
        throw new RuntimeException("Some crap");
    }



    @RequestMapping("/test")
    @ResponseBody
    public String doTest(@Autowired JavaScript javaScript) {
        javaScript.testJavaScript();
        return "ok";
    }

    @RequestMapping(value = "/test2", produces = "text/plain")
    @ResponseBody
    public String doTest2() {
        String content = TextFileReader.getText("/public/vendor/tailwindcss/tailwind.min.css");

        char[] c = content.toCharArray();

        StringBuilder b = new StringBuilder();
        StringBuilder r = new StringBuilder();
        boolean recording = true;


        for (int i = 0; i < c.length; i++) {

            char ch = c[i];

            if (recording) b.append(ch);

            if (ch == '{') {
                r.append(b);
                r.append("\n");
                b.setLength(0);
                recording = false;
            } else if (ch == '}') {
                recording = true;
            }
        }
        return r.toString();
    }
}
