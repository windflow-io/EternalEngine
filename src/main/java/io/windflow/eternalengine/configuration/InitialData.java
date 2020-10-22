package io.windflow.eternalengine.configuration;

import io.windflow.eternalengine.entities.DomainLookup;
import io.windflow.eternalengine.persistence.DomainLookupRepository;
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
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;


@Component
@PropertySource("eternalengine.${spring.profiles.active}.properties")
public class InitialData {

    private final PageRepository pageRepository;
    private final ComponentRepository componentRepository;
    private final ExtensionDataRepository extensionDataRepository;
    private final DomainLookupRepository domainLookupRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${eternalengine.resetDataOnStartup:undefined}")
    String resetDataOnStartup;

    InitialData(@Autowired PageRepository pageRepository, @Autowired ComponentRepository componentRepository, @Autowired ExtensionDataRepository extensionDataRepository, @Autowired CryptoService cryptoService, @Autowired DomainLookupRepository domainLookupRepository) {
        this.pageRepository = pageRepository;
        this.componentRepository = componentRepository;
        this.extensionDataRepository = extensionDataRepository;
        this.domainLookupRepository = domainLookupRepository;
    }

    @PostConstruct
    public void initialPage() {
        if ((resetDataOnStartup.equals("undefined") && pageRepository.count() == 0) || resetDataOnStartup.equals("true")) {
            logger.warn("Truncating pages and adding default data. Usually happens once. See prop io.windflow.resetDataOnStartup");
            pageRepository.truncate();

            /* Auth.windflow.io.local */
            savePage("auth-windflow-io.windflow.app.local", "/", Page.PageType.PageNormal, "/data/auth-windflow-io.windflow.app.local/pages/index.json");

        }
    }

    @PostConstruct
    public void initialComponents() {
        if ((resetDataOnStartup.equals("undefined") && pageRepository.count() == 0) || resetDataOnStartup.equals("true")) {
            logger.warn("Truncating components (and layouts) and adding default data. Usually happens once. See prop io.windflow.resetDataOnStartup");
            componentRepository.truncate();

            // LAYOUTS

            /* auth.windflow.io.local */
            saveComponent("auth-windflow-io.windflow.app.local", "CenteredLayout", io.windflow.eternalengine.entities.Component.ComponentType.LAYOUT, "/data/auth-windflow-io.windflow.app.local/layouts/CenteredLayout.js");
            saveComponent("auth-windflow-io.windflow.app.local", "SingleColumnLayout", io.windflow.eternalengine.entities.Component.ComponentType.LAYOUT, "/data/auth-windflow-io.windflow.app.local/layouts/SingleColumnLayout.js");

            // COMPONENTS

            /* auth.windflow.io.local */
            saveComponent("auth-windflow-io.windflow.app.local", "GithubAuth", io.windflow.eternalengine.entities.Component.ComponentType.COMPONENT, "/data/auth-windflow-io.windflow.app.local/components/GithubAuth.js");


        }
    }

    @PostConstruct
    public void temporarySetup() {

        if ((resetDataOnStartup.equals("undefined") && pageRepository.count() == 0) || resetDataOnStartup.equals("true")) {
            logger.warn("Truncating domain lookups (and layouts) and adding default data. Usually happens once. See prop io.windflow.resetDataOnStartup");
            domainLookupRepository.truncate();
        }

        domainLookupRepository.save(
                new DomainLookup("windflow-io.windflow.app.local", "windflow-io.windflow.app.local", UUID.fromString("742e256a-b3cd-45ba-adfd-ac24d7b4698c"))
        );
        domainLookupRepository.save(
                new DomainLookup("windflow.io.local", "windflow-io.windflow.app.local", UUID.fromString("742e256a-b3cd-45ba-adfd-ac24d7b4698c"))
        );
        domainLookupRepository.save(
                new DomainLookup("www.windflow.io.local", "windflow-io.windflow.app.local", UUID.fromString("742e256a-b3cd-45ba-adfd-ac24d7b4698c"))
        );
        domainLookupRepository.save(
                new DomainLookup("auth.windflow.io.local", "auth-windflow-io.windflow.app.local", UUID.fromString("742e256a-b3cd-45ba-adfd-ac24d7b4698c"))
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
