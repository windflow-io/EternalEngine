package io.windflow.eternalengine.controllers;

import io.windflow.eternalengine.utils.TextFileReader;
import io.windflow.eternalengine.entities.Page;
import io.windflow.eternalengine.persistence.PageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass())

    PageRepository pageRepository;

    public TestController(@Autowired PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @RequestMapping(value = "/create404", produces = "application/json")
    @ResponseBody
    public String create404(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        logger.warn("Simulating an error 404");
        return "{\"error\":\"404\"}";
    }

    @RequestMapping(value = "/create500", produces = "application/json")
    @ResponseBody
    public String create500(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        logger.warn("Simulating an error 500");
        return "{\"error\":\"500\"}";
    }

    @RequestMapping(value = "/createServerError", produces = "application/json")
    @ResponseBody
    public String createServerError(HttpServletResponse response) {
        throw new RuntimeException("Some crap");
    }

    @RequestMapping(value = "/createPageData", produces = "application/json")
    @ResponseBody
    public String insertData() {
        Page page1 = new Page();
        String[] jsonStrings = {
                "{\"name\":\"Mark\", \"street\":\"Egret\"}",
                "{\"name\":\"Tarryn\", \"street\":\"Egret\"}",
                "{\"name\":\"Jo\", \"street\":\"Egret\"}",
                "{\"name\":\"Fiona\", \"street\":\"Hilltop\"}",
                "{\"name\":\"Ryan\", \"street\":\"Hilltop\"}"
        };

        for (String json : jsonStrings) {
            Page p = new Page();

            p.setJson(json);
            pageRepository.save(p);
        }

        return "{\"status\":\"ok\"}";
    }

    @RequestMapping(value = "/queryPageData", produces = "application/json")
    @ResponseBody
    public String QueryData(@RequestParam String street) {
        List<Page> jsonList = pageRepository.findByStreet(street);
        return "[" + jsonList.stream().map(Page::getJson).collect(Collectors.joining(",")) + "]";
    }

    @RequestMapping(value = "/test2", produces = "text/plain")
    @ResponseBody
    public String doTest2() {
        try {
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

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = "/test3", produces = "text/plain")
    @ResponseBody
    public String doTest3() {
        return "";
    }

}
