package io.windflow.server.experiment;

import org.graalvm.polyglot.*;

import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;


@Component
public class JavaScript {

    public Integer testJavaScript(Integer number) {
        try (Context context = Context.create()) {
            Value function = context.eval("js", "x => x+1");
            assert function.canExecute();
            int x = function.execute(number).asInt();
            return x;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
