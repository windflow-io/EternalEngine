package io.windflow.eternalengine.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.jconsole.JConsoleContext;
import io.windflow.eternalengine.beans.dto.NodeUtilsError;
import io.windflow.eternalengine.beans.dto.SingleFileComponent;
import io.windflow.eternalengine.error.EternalEngineError;
import io.windflow.eternalengine.error.EternalEngineWebException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

@Service
@PropertySource("eternalengine.${spring.profiles.active}.properties")
public class VueConversionService {

    public static final Logger logger = LoggerFactory.getLogger(VueConversionService.class);

    @Value("${eternalengine.templateCompilerUrl}")
    String templateCompilerUrl;

    public String convertVueToJs(String componentName, String sfcSource) throws EternalEngineWebException {

        /** @TODO: It may be nice to put some debug code in here **/

        SingleFileComponent sfc = new SingleFileComponent(componentName, sfcSource);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(sfc.toString(), headers);

        RestTemplate template = new RestTemplate();
        try {
            ResponseEntity<String> response = template.postForEntity(templateCompilerUrl, request, String.class);
            return new ObjectMapper().readValue(response.getBody(), SingleFileComponent.class).getCode();
        } catch (ResourceAccessException ex) {
            String err = "Couldn't connect to external Vue SFC compiler at " + templateCompilerUrl;
            logger.error(err);
            ex.printStackTrace();
            throw new EternalEngineWebException(EternalEngineError.ERROR_015, err, ex);
        } catch (JsonProcessingException ex) {
            String err = "JSON Processing error";
            logger.error(err);
            ex.printStackTrace();
            throw new EternalEngineWebException(EternalEngineError.ERROR_015, err, ex);
        } catch (HttpServerErrorException.InternalServerError ex) {
            String errorResponse = ex.getMessage();
            String json = errorResponse.substring(errorResponse.indexOf('{'), errorResponse.lastIndexOf('}') + 1);
            try {
                NodeUtilsError objError = new ObjectMapper().readValue(json, NodeUtilsError.class);
                throw new EternalEngineWebException(EternalEngineError.ERROR_015, objError.getMessage());
            } catch (JsonProcessingException jsonEx) {
                String err = "Node Utils API failed but could not translate error";
                logger.error(err);
                jsonEx.printStackTrace();
                throw new EternalEngineWebException(EternalEngineError.ERROR_015, err + " " + ex.getMessage());
            }
        }
    }
}
