package io.windflow.eternalengine.controllers;


import io.windflow.eternalengine.entities.ExtensionData;
import io.windflow.eternalengine.extensions.framework.Actionable;
import io.windflow.eternalengine.extensions.framework.Datafiable;
import io.windflow.eternalengine.extensions.framework.Requestable;
import io.windflow.eternalengine.extensions.framework.Respondable;
import io.windflow.eternalengine.persistence.ExtensionDataRepository;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@RestController
@PropertySource("classpath:secret.properties")
public class ExtensionController {

    @Value("${io.windflow.encryption.password}")
    String encryptionPassword;

    ExtensionDataRepository extensionDataRepository;

    public ExtensionController(@Autowired ExtensionDataRepository extensionDataRepository) {
        this.extensionDataRepository = extensionDataRepository;
    }

    @RequestMapping("/api/extensions/{fullyQualifiedClassName}/{actionName}")
    @ResponseBody
    public String performAction(@PathVariable("fullyQualifiedClassName") String fullyQualifiedClassName, @PathVariable("actionName") String actionName, HttpServletRequest request, HttpServletResponse response) {

        try {

            /** @TODO: Use a classloader that protects the system from malicious classes. **/
            /** @TODO: Cache the object in an extension singleton **/

            Class<?> pluginClass = Class.forName(fullyQualifiedClassName);
            Object plugin = pluginClass.getConstructor().newInstance();

            if (plugin instanceof Datafiable) {

                HashMap<String, String> keyValuePairs = new HashMap<>();
                BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
                textEncryptor.setPassword(encryptionPassword);

                List<ExtensionData> extensionDataList = extensionDataRepository.findByFullyQualifiedClassName(fullyQualifiedClassName);
                for (ExtensionData extensionData : extensionDataList) {
                    if (extensionData.isKeyEncrypted()) {
                        String encrypted = extensionData.getValue();
                        String decrypted = textEncryptor.decrypt(encrypted);
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

        } catch (Exception ex) {
            /*@TODO Proper error handling here please */
            ex.printStackTrace();
            return "{\"status\":\"An error occurred\"}";


        }

        return "{\"status\":\"No action performed\"}";

    }

}
