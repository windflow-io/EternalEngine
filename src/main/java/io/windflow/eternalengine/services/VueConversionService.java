package io.windflow.eternalengine.services;

import org.springframework.stereotype.Service;

@Service
public class VueConversionService {

    public String convertVueToJs(String vue) {
        return "export default {}";
    }


}
