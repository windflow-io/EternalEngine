package io.windflow.server.experiment;

import io.windflow.server.TextFileReader;
import org.graalvm.polyglot.*;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;


@Component
public class JavaScript {

    public void testJavaScript() {

        try(Context ctx = Context.newBuilder("js").allowIO(true).build()) {
            try {
                File sourceCode = new ClassPathResource("/js/index.js").getFile();
                Source src = Source.newBuilder("js", sourceCode).mimeType("application/javascript+module").build();
                ctx.eval(src);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}
