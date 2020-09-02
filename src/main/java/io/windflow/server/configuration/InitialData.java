package io.windflow.server.configuration;

import io.windflow.server.entities.Page;
import io.windflow.server.persistence.PageRepository;
import io.windflow.server.utils.TextFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;


@Component
public class InitialData {

    PageRepository pageRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${io.windflow.resetDataOnStartup:undefined}")
    String resetDataOnStartup;

    InitialData(@Autowired  PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @PostConstruct
    public void initialPage() {
        if ((resetDataOnStartup.equals("undefined") && pageRepository.count() == 0) || resetDataOnStartup.equals("true")) {
            logger.info("Truncating pages and adding default data. Usually happens once. See prop io.windflow.resetDataOnStartup");
            pageRepository.truncate();
            savePage("localhost", "/", Page.PageType.PageNormal, "/data/localhost/index.json");
            savePage("localhost", "/about", Page.PageType.PageNormal, "/data/localhost/about.json");
            savePage("localhost", "/contact", Page.PageType.PageNormal, "/data/localhost/contact.json");
            savePage("localhost", null, Page.PageType.Page404, "/data/localhost/404.json");
        }
    }

    @PostConstruct
    public void initialComponents() {

    }

    private void savePage(String domain, String path, Page.PageType type, String filePath) {
        try {
            pageRepository.save(new Page(domain, path, type, TextFileReader.getText(filePath)));
        } catch (IOException ex) {
            logger.warn("Could not load file: " + ex.getMessage() + ". Ignoring, but be prepared for an error page on first visit!");
        }
    }

}
