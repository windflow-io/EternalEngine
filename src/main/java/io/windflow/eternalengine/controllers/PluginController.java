package io.windflow.eternalengine.controllers;


import io.windflow.eternalengine.plugins.framework.Plugin;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;


@RestController
public class PluginController {

    @RequestMapping("/api/plugins/{fullyQualifiedClassName}/{actionName}")
    @ResponseBody
    public String performAction(@PathVariable("fullyQualifiedClassName") String fullyQualifiedClassName, @PathVariable("actionName") String actionName) {

        // Using "io.windflow.eternalengine.plugins.api.OpenIDPlugin"

        try {

            Class<?> pluginClass = Class.forName(fullyQualifiedClassName);
            Object plugin = pluginClass.getConstructor().newInstance();
            Method action = pluginClass.getDeclaredMethod(actionName);
            String response = (String) action.invoke(plugin, null);
            return "Plugin Says: " + response;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Error";

    }

}
