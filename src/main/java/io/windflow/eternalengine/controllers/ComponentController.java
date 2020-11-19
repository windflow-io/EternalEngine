package io.windflow.eternalengine.controllers;

import io.windflow.eternalengine.entities.Component;
import io.windflow.eternalengine.entities.DomainLookup;
import io.windflow.eternalengine.error.EternalEngineError;
import io.windflow.eternalengine.error.EternalEngineNotFoundException;
import io.windflow.eternalengine.error.EternalEngineWebException;
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

    public ComponentController(@Autowired ComponentRepository componentRepository, @Autowired VueConversionService vueConversionService, @Autowired DomainFinder domainFinder) {
        this.componentRepository = componentRepository;
        this.vueConversionService = vueConversionService;
        this.domainFinder = domainFinder;
    }

    /** RENDERER GET **/
    @RequestMapping(method = RequestMethod.GET, value = "/components/{componentIdentifier:^.+\\.js$}", produces = "text/javascript")
    @ResponseBody
    public String getComponent( @PathVariable("componentIdentifier") String componentIdentifier) {

        DomainFinder.NamespaceAndComponentName spaceName = DomainFinder.extractParts(componentIdentifier);

        logger.debug("Component Request:" + spaceName.getNamespace() + "." + spaceName.getComponentName());

        Optional<Component> optComponent = componentRepository.findByNamespaceAndComponentName(spaceName.getNamespace(), spaceName.getComponentName());
        if (optComponent.isPresent()) {
            return optComponent.get().getJavascript();
        } else {
            String errorDetail = "007 Component not found in database. Namespace: " + spaceName.getNamespace() + " and component name: " + spaceName.getComponentName();
            logger.warn(errorDetail);
            throw new EternalEngineNotFoundException(EternalEngineError.ERROR_008, errorDetail);
        }

    }

    /*** EDITOR GET ***/

    @RequestMapping(method = RequestMethod.GET, value = {"/api/components/{componentIdentifier}"}, produces = "application/json")
    @ResponseBody
    public Component getComponentForEditing(@PathVariable(value = "componentIdentifier", required = false) String componentIdentifier, HttpServletRequest request) {

        DomainFinder.NamespaceAndComponentName spaceName = DomainFinder.extractParts(componentIdentifier);

        Optional<Component> optComponent = componentRepository.findByNamespaceAndComponentName(spaceName.getNamespace(), spaceName.getComponentName());
        if (optComponent.isPresent()) {
            return optComponent.get();
        } else {
            String errorDetail = "007 Component not found in database. Namespace: " + spaceName.getNamespace() + " and component name: " + spaceName.getComponentName();
            logger.warn(errorDetail);
            throw new EternalEngineNotFoundException(EternalEngineError.ERROR_008, errorDetail);
        }
    }

    /** EDITOR PUT **/

    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST}, value = {"/api/components"}, produces = "application/json")
    @ResponseBody
    public String saveComponent(@RequestBody Component componentArrived, HttpServletRequest request) {

        String domain = request.getServerName();

        Optional<DomainLookup> optDomain = domainFinder.lookup(domain);

        if (optDomain.isEmpty()) throw new EternalEngineWebException(EternalEngineError.ERROR_016, domain + " has not been configured.");

        String siteId = optDomain.get().getSiteId();
        if (!siteId.equals(componentArrived.getNamespace())) {
            componentArrived.setNamespace(siteId);
            logger.debug("Copying component " + domain + "." + componentArrived.getComponentName() + " to " + siteId + "." + componentArrived.getComponentName());
        }

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
            componentToSave.setNamespace(domainFinder.getSite(request).getSiteId());

        }

        componentToSave.setJavascript(vueConversionService.convertVueToJs(componentToSave.getComponentName(), componentToSave.getSingleFileComponent()));
        return componentRepository.save(componentToSave).toString();
    }

    @ExceptionHandler(EternalEngineNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpError handleWindflowNotFoundException(EternalEngineNotFoundException windEx) {
        windEx.printStackTrace();
        return new HttpError(HttpStatus.NOT_FOUND.value(), windEx.getWindflowError(), windEx.getDetailOnly());
    }

    @ExceptionHandler(EternalEngineWebException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public HttpError handleEternalEngineWebException(EternalEngineWebException windEx) {
        return new HttpError(HttpStatus.INTERNAL_SERVER_ERROR.value(), windEx.getWindflowError(), windEx.getDetailOnly());
    }

}
