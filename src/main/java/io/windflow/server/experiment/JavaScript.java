package io.windflow.server.experiment;

import org.graalvm.options.OptionDescriptor;
import org.graalvm.polyglot.*;

import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.HashMap;


@Component
public class JavaScript {

    String js = "import Purgecss from 'purgecss'\n" +
            "const purgeCss = new Purgecss({\n" +
            "  content: ['**/*.html'],\n" +
            "  css: ['**/*.css']\n" +
            "})\n" +
            "const purgecssResult = purgecss.purge()";

    public void testJavaScript() {

        String node_modules = System.getProperty("user.dir") + "/node_modules";

        Context cx = Context.newBuilder("js")
                .allowAllAccess(true)
                .allowIO(true)
                .allowHostAccess(true)
                .option("js.commonjs-require", "true")
                .option("js.commonjs-require-cwd", node_modules)
                .build();
        cx.eval("js", "" +
                "var browserify = require('browserify');" +
                "var b = browserify(require('purgecss')).bundle();" +
                "console.log (b);");
    }
}
