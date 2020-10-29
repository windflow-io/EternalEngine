package io.windflow.eternalengine.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.windflow.eternalengine.beans.dto.SingleFileComponent;
import io.windflow.eternalengine.error.EternalEngineError;
import io.windflow.eternalengine.error.EternalEngineWebException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;

@Service
@PropertySource("classpath:eternalengine.${spring.profiles.active}.properties")
public class VueConversionService {

    @Value("${eternalengine.templateCompilerUrl}")
    String templateCompilerUrl;

    public String convertVueToJs(String componentName, String sfcSource) throws EternalEngineWebException {
        SingleFileComponent sfc = new SingleFileComponent(componentName, sfcSource);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(sfc.toString(), headers);

        RestTemplate template = new RestTemplate();
        try {
            ResponseEntity<String> response = template.postForEntity(templateCompilerUrl, request, String.class);
            return new ObjectMapper().readValue(response.getBody(), SingleFileComponent.class).getCode();
        } catch (ResourceAccessException ex) {
            System.out.println("HERE 111");
            ex.printStackTrace();
            throw new EternalEngineWebException(EternalEngineError.ERROR_015, "Couldn't connect to external Vue SFC compiler at " + templateCompilerUrl, ex);
        } catch (JsonProcessingException ex) {
            throw new EternalEngineWebException(EternalEngineError.ERROR_015, "JSON Processing error", ex);
        }
    }
}
