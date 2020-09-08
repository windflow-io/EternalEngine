package io.windflow.eternalengine.controllers;

import io.windflow.eternalengine.entities.Component;
import io.windflow.eternalengine.error.WindflowError;
import io.windflow.eternalengine.error.WindflowNotFoundException;
import io.windflow.eternalengine.persistence.ComponentRepository;
import io.windflow.eternalengine.utils.HttpError;
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

    @RequestMapping(value = {"/api/components/{namespace}/{filename:^.+\\.mjs$}","/api/layouts/{namespace}/{filename:^.+\\.mjs$}"}, produces = "text/javascript")
    @ResponseBody
    public String mjs(@PathVariable("namespace") String namespace, @PathVariable("filename") String componentFilname) {

        String componentName = componentFilname.replace(".mjs", "");

        Optional<Component> component = componentRepository.findByNamespaceAndComponentName(namespace, componentName);
        if (component.isPresent()) {
            return component.get().getJavaScript();
        } else {
            logger.warn("007 Component not found in database. Namespace: " + namespace + " and component name: " + componentName);
            throw new WindflowNotFoundException(WindflowError.ERROR_007);
        }

    }

    @ExceptionHandler(WindflowNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpError handleWindflowNotFoundException(WindflowNotFoundException windEx) {
        return new HttpError(HttpStatus.NOT_FOUND.value(), windEx.getWindflowError(), windEx.getDetailOnly());
    }

}