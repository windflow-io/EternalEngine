package io.windflow.eternalengine.configuration;

import io.windflow.eternalengine.entities.DomainLookup;
import io.windflow.eternalengine.entities.EternalEngineUser;
import io.windflow.eternalengine.persistence.*;
import io.windflow.eternalengine.services.CryptoService;
import io.windflow.eternalengine.entities.ExtensionData;
import io.windflow.eternalengine.entities.Page;
import io.windflow.eternalengine.utils.TextFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


@Component
@PropertySource("eternalengine.${spring.profiles.active}.properties")
public class InitialData {

    private final PageRepository pageRepository;
    private final ComponentRepository componentRepository;
    private final ExtensionDataRepository extensionDataRepository;
    private final DomainLookupRepository domainLookupRepository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${eternalengine.resetDataOnStartup:undefined}")
    String resetDataOnStartup;

    @Value(value = "${eternalengine.systemNamespace}")
    String systemNamespace;

    @Value(value = "${eternalengine.siteOwnerGithubEmail}")
    String ownerEmail;

    InitialData(@Autowired PageRepository pageRepository, @Autowired ComponentRepository componentRepository, @Autowired ExtensionDataRepository extensionDataRepository, @Autowired CryptoService cryptoService, @Autowired DomainLookupRepository domainLookupRepository, @Autowired UserRepository userRepository) {
        this.pageRepository = pageRepository;
        this.componentRepository = componentRepository;
        this.extensionDataRepository = extensionDataRepository;
        this.domainLookupRepository = domainLookupRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initialPage() {
        if ((resetDataOnStartup.equals("undefined") && pageRepository.count() == 0) || resetDataOnStartup.equals("true")) {
            logger.warn("Truncating pages and adding default data. Usually happens once. See prop io.windflow.resetDataOnStartup");
            pageRepository.truncate();

            /* Auth.windflow.io */
            savePage("auth-windflow-io.windflow.app", "/", Page.PageType.PageNormal, "/data/auth-windflow-io.windflow.app/pages/index.json");

            // SYSTEM

            savePage(systemNamespace, "/", Page.PageType.Page404, "/data/" + systemNamespace + "/pages/404.json");

        }
    }

    @PostConstruct
    public void initialComponents() {
        if ((resetDataOnStartup.equals("undefined") && componentRepository.count() == 0) || resetDataOnStartup.equals("true")) {
            logger.warn("Truncating components (and layouts) and adding default data. Usually happens once. See prop io.windflow.resetDataOnStartup");
            componentRepository.truncate();

            // LAYOUTS

            /* auth.windflow.io */
            saveComponent("auth-windflow-io.windflow.app", "CenteredLayout", io.windflow.eternalengine.entities.Component.ComponentType.LAYOUT, "/data/auth-windflow-io.windflow.app/layouts/CenteredLayout.js");
            saveComponent("auth-windflow-io.windflow.app", "SingleColumnLayout", io.windflow.eternalengine.entities.Component.ComponentType.LAYOUT, "/data/auth-windflow-io.windflow.app/layouts/SingleColumnLayout.js");
            saveComponent(systemNamespace, "CenteredLayout", io.windflow.eternalengine.entities.Component.ComponentType.LAYOUT, "/data/" + systemNamespace + "/layouts/CenteredLayout.js");

            // COMPONENTS

            /* auth.windflow.io */
            saveComponent("auth-windflow-io.windflow.app", "GithubAuth", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/auth-windflow-io.windflow.app/components/GithubAuth.js", "/data/auth-windflow-io.windflow.app/components/GithubAuth.vue");

            // SYSTEM

            saveComponent(systemNamespace, "NotFoundDisplay", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/" + systemNamespace + "/components/NotFoundDisplay.js");

        }
    }

    @PostConstruct
    public void temporarySetup() {

        if ((resetDataOnStartup.equals("undefined") && domainLookupRepository.count() == 0) || resetDataOnStartup.equals("true")) {
            logger.warn("Truncating domain lookups (and layouts) and adding default data. Usually happens once. See prop io.windflow.resetDataOnStartup");
            domainLookupRepository.truncate();
        }

        domainLookupRepository.save(
                new DomainLookup("windflow-io.windflow.app.local", "windflow-io.windflow.app", ownerEmail)
        );
        domainLookupRepository.save(
                new DomainLookup("windflow.io.local", "windflow-io.windflow.app", ownerEmail)
        );
        domainLookupRepository.save(
                new DomainLookup("www.windflow.io.local", "windflow-io.windflow.app", ownerEmail)
        );
        domainLookupRepository.save(
                new DomainLookup("auth.windflow.io.local", "auth-windflow-io.windflow.app", ownerEmail)
        );
    }


    /* Private methods */

    private void savePage(String domain, String path, Page.PageType type, String filePath) {
        try {
            pageRepository.save(new Page(domain, path, type, TextFileReader.getText(filePath)));
        } catch (IOException ex) {
            logger.warn("Could not load file: " + ex.getMessage() + ". Ignoring, but be prepared for an error page on first visit!");
        }
    }

    private void saveComponent(String namespace, String name, io.windflow.eternalengine.entities.Component.ComponentType componentType, String jsPath) {
        saveComponent(namespace, name, componentType, jsPath, null);
    }

    private void saveComponent(String namespace, String name, io.windflow.eternalengine.entities.Component.ComponentType componentType, String jsPath, String vuePath) {
        try {
            componentRepository.save(new io.windflow.eternalengine.entities.Component(namespace, name, componentType, TextFileReader.getText(jsPath), TextFileReader.getText(vuePath)));
        } catch (IOException ex) {
            logger.warn("Could not load file: " + ex.getMessage() + ". Ignoring, but be prepared for an error page on first visit!");
        }

    }

    private void saveExtensionData(String className, String key, String value, Boolean encrypted) {

        extensionDataRepository.save(new ExtensionData(className, key, value, encrypted));
    }

}
