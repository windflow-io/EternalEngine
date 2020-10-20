package io.windflow.eternalengine.configuration;

import io.windflow.eternalengine.services.CryptoService;
import io.windflow.eternalengine.entities.ExtensionData;
import io.windflow.eternalengine.entities.Page;
import io.windflow.eternalengine.persistence.ComponentRepository;
import io.windflow.eternalengine.persistence.ExtensionDataRepository;
import io.windflow.eternalengine.persistence.PageRepository;
import io.windflow.eternalengine.utils.TextFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;


@Component
public class InitialData {

    private final CryptoService cryptoService;
    private final PageRepository pageRepository;
    private final ComponentRepository componentRepository;
    private final ExtensionDataRepository extensionDataRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${io.windflow.eternalengine.resetDataOnStartup:undefined}")
    String resetDataOnStartup;

    InitialData(@Autowired PageRepository pageRepository, @Autowired ComponentRepository componentRepository, @Autowired ExtensionDataRepository extensionDataRepository, @Autowired CryptoService cryptoService) {
        this.pageRepository = pageRepository;
        this.componentRepository = componentRepository;
        this.extensionDataRepository = extensionDataRepository;
        this.cryptoService = cryptoService;
    }

    @PostConstruct
    public void initialPage() {
        if ((resetDataOnStartup.equals("undefined") && pageRepository.count() == 0) || resetDataOnStartup.equals("true")) {
            logger.warn("Truncating pages and adding default data. Usually happens once. See prop io.windflow.resetDataOnStartup");
            pageRepository.truncate();

            /* Auth.windflow.local */
            savePage("auth.windflow.local", "/", Page.PageType.PageNormal, "/data/auth.windflow.local/pages/index.json");

            /* Localhost */
            savePage("localhost", "/", Page.PageType.PageNormal, "/data/localhost/pages/index.json");
            savePage("localhost", "/about", Page.PageType.PageNormal, "/data/localhost/pages/about.json");
            savePage("localhost", "/contact", Page.PageType.PageNormal, "/data/localhost/pages/contact.json");
            savePage("localhost", "/auth", Page.PageType.PageNormal, "/data/localhost/pages/auth.json");
            savePage("localhost", "/upload", Page.PageType.PageNormal, "/data/localhost/pages/upload.json");
            savePage("localhost", "/gallery", Page.PageType.PageNormal, "/data/localhost/pages/gallery.json");
            savePage("localhost", null, Page.PageType.Page404, "/data/localhost/pages/404.json");
        }
    }

    @PostConstruct
    public void initialComponents() {
        if ((resetDataOnStartup.equals("undefined") && pageRepository.count() == 0) || resetDataOnStartup.equals("true")) {
            logger.warn("Truncating components (and layouts) and adding default data. Usually happens once. See prop io.windflow.resetDataOnStartup");
            componentRepository.truncate();

            // Layouts

            /* auth.windflow.local */
            saveComponent("auth.windflow.local", "CenteredLayout", io.windflow.eternalengine.entities.Component.ComponentType.LAYOUT, "/data/auth.windflow.local/layouts/CenteredLayout.js");
            saveComponent("auth.windflow.local", "SingleColumnLayout", io.windflow.eternalengine.entities.Component.ComponentType.LAYOUT, "/data/auth.windflow.local/layouts/SingleColumnLayout.js");

            /* localhost */
            saveComponent("localhost", "CenteredLayout", io.windflow.eternalengine.entities.Component.ComponentType.LAYOUT, "/data/localhost/layouts/CenteredLayout.js");
            saveComponent("localhost", "LeftMenuLayout", io.windflow.eternalengine.entities.Component.ComponentType.LAYOUT, "/data/localhost/layouts/LeftMenuLayout.js");
            saveComponent("localhost", "SingleColumnLayout", io.windflow.eternalengine.entities.Component.ComponentType.LAYOUT, "/data/localhost/layouts/SingleColumnLayout.js");

            // Components

            /* auth.windflow.local */
            saveComponent("auth.windflow.local", "GithubAuth", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/auth.windflow.local/components/GithubAuth.js");

            /* localhost */
            saveComponent("localhost", "ContactForm", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/localhost/components/ContactForm.js");
            saveComponent("localhost", "HeaderAbout", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/localhost/components/HeaderAbout.js");
            saveComponent("localhost", "HeaderContact", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/localhost/components/HeaderContact.js");
            saveComponent("localhost", "HeaderHome", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/localhost/components/HeaderHome.js");
            saveComponent("localhost", "LipsumContent", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/localhost/components/LipsumContent.js");
            saveComponent("localhost", "MainContent", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/localhost/components/MainContent.js");
            saveComponent("localhost", "MainContact", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/localhost/components/MainContact.js");
            saveComponent("localhost", "PageNotFoundMessage", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/localhost/components/PageNotFoundMessage.js");
            saveComponent("localhost", "SideMenu", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/localhost/components/SideMenu.js");
            saveComponent("localhost", "HeroBlock", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/localhost/components/HeroBlock.js");
            saveComponent("localhost", "FeatureSection", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/localhost/components/FeatureSection.js");
            saveComponent("localhost", "CtaSection", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/localhost/components/CtaSection.js");

        }
    }

    private void savePage(String domain, String path, Page.PageType type, String filePath) {
        try {
            pageRepository.save(new Page(domain, path, type, TextFileReader.getText(filePath)));
        } catch (IOException ex) {
            logger.warn("Could not load file: " + ex.getMessage() + ". Ignoring, but be prepared for an error page on first visit!");
        }
    }

    private void saveComponent(String namespace, String name, io.windflow.eternalengine.entities.Component.ComponentType componentType, String filePath) {
        try {
            componentRepository.save(new io.windflow.eternalengine.entities.Component(namespace, name, componentType, TextFileReader.getText(filePath)));
        } catch (IOException ex) {
            logger.warn("Could not load file: " + ex.getMessage() + ". Ignoring, but be prepared for an error page on first visit!");
        }
    }

    private void saveExtensionData(String className, String key, String value, Boolean encrypted) {

        extensionDataRepository.save(new ExtensionData(className, key, value, encrypted));
    }

}
