package io.windflow.server.experiment;

import org.graalvm.polyglot.*;

import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;


@Component
public class JavaScript {

    @PostConstruct
    public void testJavaScript() {
        try (Context context = Context.create()) {
            Value function = context.eval("js", "x => x+1");
            assert function.canExecute();
            int x = function.execute(41).asInt();
            assert x == 42;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
