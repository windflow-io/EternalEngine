package io.windflow.server.controllers;

import io.windflow.server.StubReader;
import io.windflow.server.experiment.JavaScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @RequestMapping("/test")
    @ResponseBody
    public String doTest(@Autowired JavaScript javaScript, @RequestParam("number") String number) {
        Integer result = javaScript.testJavaScript(Integer.parseInt(number));
        return number + " + 1 = " + result + " says Javascript";
    }

    @RequestMapping(value = "/test2", produces = "text/plain")
    @ResponseBody
    public String doTest2() {
        String content = StubReader.loadStub("/public/vendor/tailwindcss/tailwind.min.css");

        char[] c = content.toCharArray();

        StringBuffer b = new StringBuffer();
        StringBuffer r = new StringBuffer();
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
