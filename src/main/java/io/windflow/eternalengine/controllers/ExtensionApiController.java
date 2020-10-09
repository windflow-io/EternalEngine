package io.windflow.eternalengine.controllers;


import io.windflow.eternalengine.extensions.framework.*;
import io.windflow.eternalengine.services.CryptoService;
import io.windflow.eternalengine.entities.ExtensionData;
import io.windflow.eternalengine.persistence.ExtensionDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

@RestController
public class ExtensionApiController {

    ExtensionDataRepository extensionDataRepository;
    CryptoService cryptoService;

    public ExtensionApiController(@Autowired ExtensionDataRepository extensionDataRepository, @Autowired CryptoService cryptoService) {
        this.extensionDataRepository = extensionDataRepository;
        this.cryptoService = cryptoService;
    }

    @RequestMapping("/api/extensions/{fullyQualifiedClassName}/{actionName}")
    @ResponseBody
    public String performAction(@PathVariable("fullyQualifiedClassName") String fullyQualifiedClassName, @PathVariable("actionName") String actionName, HttpServletRequest request, HttpServletResponse response) {

        try {

            /** @TODO: Use a classloader that protects the system from malicious classes. **/
            /** @TODO: Cache the object in an extension singleton **/

            Object plugin = null;
            try {
                Class<?> pluginClass = Class.forName(fullyQualifiedClassName);
                plugin = pluginClass.getConstructor().newInstance();
            } catch (ClassNotFoundException ex) {
                throw new ExtensionException("Error loading extension. Is " + fullyQualifiedClassName + " on the classpath and spelled correctly?" , ex);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
                throw new ExtensionException("Error instantiating extension.", ex);
            }

            if (plugin instanceof Datafiable) {

                HashMap<String, String> keyValuePairs = new HashMap<>();

                List<ExtensionData> extensionDataList = extensionDataRepository.findByFullyQualifiedClassName(fullyQualifiedClassName);
                for (ExtensionData extensionData : extensionDataList) {
                    if (extensionData.isKeyEncrypted()) {
                        String encrypted = extensionData.getValue();
                        String decrypted = cryptoService.decrypt(encrypted);
                        keyValuePairs.put(extensionData.getKey(), decrypted);
                    } else {
                        keyValuePairs.put(extensionData.getKey(), extensionData.getValue());
                    }
                }
                ((Datafiable) plugin).injectData(keyValuePairs);
            }

            if (plugin instanceof Requestable) {
                ((Requestable) plugin).injectRequest(request);
            }

            if (plugin instanceof Respondable) {
                ((Respondable) plugin).injectResponse(response);
            }

            if (plugin instanceof Actionable) {
                return ((Actionable) plugin).performAction(actionName, "");
            }

            return "{\"status\":\"error\",\"message\":\"Could not execute plugin as it does not implement Actionable\"}";

        } catch (ExtensionException ex) {
            ex.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"" + ex.getMessage() + "\"}";
        }

    }

}
