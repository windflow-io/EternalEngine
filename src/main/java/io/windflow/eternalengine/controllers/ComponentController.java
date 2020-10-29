package io.windflow.eternalengine.controllers;

import io.windflow.eternalengine.entities.Component;
import io.windflow.eternalengine.entities.DomainLookup;
import io.windflow.eternalengine.error.EternalEngineError;
import io.windflow.eternalengine.error.EternalEngineNotFoundException;
import io.windflow.eternalengine.persistence.ComponentRepository;
import io.windflow.eternalengine.beans.dto.HttpError;
import io.windflow.eternalengine.services.DomainFinder;
import io.windflow.eternalengine.services.VueConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
public class ComponentController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ComponentRepository componentRepository;
    private final DomainFinder domainFinder;
    private final VueConversionService vueConversionService;

    public ComponentController(@Autowired ComponentRepository componentRepository, @Autowired DomainFinder domainFinder, @Autowired VueConversionService vueConversionService) {
        this.componentRepository = componentRepository;
        this.domainFinder = domainFinder;
        this.vueConversionService = vueConversionService;
    }

    /** RENDERER GET **/

    @RequestMapping(method = RequestMethod.GET, value = {"/components/{namespace}/{filename:^.+\\.js$}", "/components/{filename:^.+\\.js$}"}, produces = "text/javascript")
    @ResponseBody
    public String getComponent(@PathVariable(value = "namespace", required = false) String namespace, @PathVariable("filename") String componentFilename, HttpServletRequest request) {

        System.out.println("HERE HERE HERE");

        if (namespace == null) {
            DomainLookup site = domainFinder.getSite(request);
            namespace = site.getSiteId();
        }

        String componentName = componentFilename.replaceFirst(".js", "");

        Optional<Component> optComponent = componentRepository.findByNamespaceAndComponentName(namespace, componentName);
        if (optComponent.isPresent()) {
            return optComponent.get().getJavaScript();
        } else {
            String errorDetail = "007 Component not found in database. Namespace: " + namespace + " and component name: " + componentName;
            logger.warn(errorDetail);
            throw new EternalEngineNotFoundException(EternalEngineError.ERROR_008, errorDetail);
        }

    }

    /*** EDITOR GET ***/

    @RequestMapping(method = RequestMethod.GET, value = {"/api/components/{namespace}/{filename}", "/api/components/{filename}"}, produces = "application/json")
    @ResponseBody
    public Component getComponentForEditing(@PathVariable(value = "namespace", required = false) String namespace, @PathVariable("filename") String componentFilename, HttpServletRequest request) {

        if (namespace == null) {
            DomainLookup site = domainFinder.getSite(request);
            namespace = site.getSiteId();
        }

        String componentName = componentFilename.replaceFirst(".js", "");

        Optional<Component> optComponent = componentRepository.findByNamespaceAndComponentName(namespace, componentName);
        if (optComponent.isPresent()) {
            return optComponent.get();
        } else {
            String errorDetail = "007 Component not found in database. Namespace: " + namespace + " and component name: " + componentName;
            logger.warn(errorDetail);
            throw new EternalEngineNotFoundException(EternalEngineError.ERROR_008, errorDetail);
        }
    }

    /** RENDERER PUT **/     /** CRAPPY ERROR **/

    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST}, value = {"/api/components"}, produces = "text/javascript")
    @ResponseBody
    public String saveComponent(HttpServletRequest request, @RequestBody Component componentArrived) {

        Optional<Component> optComponentOnDisk = componentRepository.findByNamespaceAndComponentName(componentArrived.getNamespace(), componentArrived.getComponentName());
        Component componentToSave;

        if (optComponentOnDisk.isPresent()) {
            componentToSave = optComponentOnDisk.get();
            componentToSave.setComponentName(componentArrived.getComponentName());
            componentToSave.setNamespace(componentArrived.getNamespace());
            componentToSave.setComponentType(componentArrived.getComponentType());
            componentToSave.setSingleFileComponent(componentArrived.getSingleFileComponent());
        } else {
            componentToSave = componentArrived;
        }

        componentToSave.setJavaScript(vueConversionService.convertVueToJs(componentToSave.getSingleFileComponent()));

        componentRepository.save(componentToSave);

        return "{}";
    }

    @ExceptionHandler(EternalEngineNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpError handleWindflowNotFoundException(EternalEngineNotFoundException windEx) {
        windEx.printStackTrace();
        return new HttpError(HttpStatus.NOT_FOUND.value(), windEx.getWindflowError(), windEx.getDetailOnly());
    }

}
