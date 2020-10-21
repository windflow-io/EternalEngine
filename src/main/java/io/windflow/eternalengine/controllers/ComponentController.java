package io.windflow.eternalengine.controllers;

import io.windflow.eternalengine.entities.Component;
import io.windflow.eternalengine.error.EternalEngineError;
import io.windflow.eternalengine.error.EternalEngineNotFoundException;
import io.windflow.eternalengine.persistence.ComponentRepository;
import io.windflow.eternalengine.beans.dto.HttpError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ComponentController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ComponentRepository componentRepository;

    public ComponentController(@Autowired ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    /***
     * Get component from server
     * @param namespace component namespace (domain) eg: com.mysite.components
     * @param componentFilename the filename of the component ending in .js
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = {"/api/components/{namespace}/{filename:^.+\\.js$}","/api/layouts/{namespace}/{filename:^.+\\.js$}"}, produces = "text/javascript")
    @ResponseBody
    public String getComponent(@PathVariable("namespace") String namespace, @PathVariable("filename") String componentFilename) {

        String componentName = componentFilename.replace(".js", "");

        Optional<Component> optComponent = componentRepository.findByNamespaceAndComponentName(namespace, componentName);
        if (optComponent.isPresent()) {
            return optComponent.get().getJavaScript();
        } else {
            logger.warn("007 Component not found in database. Namespace: " + namespace + " and component name: " + componentName);
            throw new EternalEngineNotFoundException(EternalEngineError.ERROR_008);
        }

    }

    /***
     * Put a component on the server
     * @param namespace component namespace (domain) eg: com.mysite.components
     * @param componentFilename the filename of the component ending in .js
     * @return
     */
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST}, value = "/api/{componentType}/{namespace}/{filename:^.+\\.js$}", produces = "text/javascript")
    @ResponseBody
    public String saveComponent(@PathVariable("namespace") String componentType, @PathVariable("namespace") String namespace, @PathVariable("filename") String componentFilename, @RequestBody String javaScript) {

        String componentName = componentFilename.replace(".js", "");
        Component component;

        Optional<Component> optComponent = componentRepository.findByNamespaceAndComponentName(namespace, componentName);
        if (optComponent.isPresent()) {
            component = optComponent.get();
        } else {
            component = new Component();
            component.setNamespace(namespace);
            component.setComponentName(componentName);
            component.setComponentType(componentType.equals("layouts") ? Component.ComponentType.LAYOUT : Component.ComponentType.COMPONENT);
        }

        component.setJavaScript(javaScript);

        return componentRepository.save(component).getJavaScript();

    }

    @ExceptionHandler(EternalEngineNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpError handleWindflowNotFoundException(EternalEngineNotFoundException windEx) {
        windEx.printStackTrace();
        return new HttpError(HttpStatus.NOT_FOUND.value(), windEx.getWindflowError(), windEx.getDetailOnly());
    }

}
