package io.windflow.server.configuration;

import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServerConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {

        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        mappings.add("mjs", "text/javascript");
        factory.setMimeMappings(mappings);
    }
}
