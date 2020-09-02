package io.windflow.server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.windflow.server.beans.PageData;
import io.windflow.server.entities.Page;
import io.windflow.server.persistence.PageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class WebController {

    PageRepository pageRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public WebController(@Autowired PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @GetMapping(value = "/")
    public String root(HttpServletRequest request, Model model) {
        return spa(request, model);
    }

    @GetMapping(value = "/**/{regex:[-a-zA-Z0-9]*}")
    public String spa(HttpServletRequest request, Model model) {

        Page pageToServe = null;

        Optional<Page> optPage = pageRepository.findByDomainAndPath(request.getServerName(), request.getServletPath());
        if (optPage.isPresent()) {
            pageToServe = optPage.get();
        } else {
            Optional<Page> opt404Page = pageRepository.findByDomainAndType(request.getServerName(), Page.PageType.Page404);
            if (opt404Page.isPresent()) {
                pageToServe = opt404Page.get();
            }
        }

        if (pageToServe != null) {
            try {
                PageData pageData = new ObjectMapper().readValue(pageToServe.getJson(), PageData.class);
                System.out.println(pageData);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }



        return "spa";

    }

    /**@TODO: What do we do if we cannot interpret the page data? **/

//    @ExceptionHandler(RuntimeException.class)
//    @ResponseBody
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public HttpError handleRuntimeException(RuntimeException ex) {
//        return new HttpError(HttpStatus.INTERNAL_SERVER_ERROR.value(), WindflowError.ERROR_001, ex.getMessage());
//    }

}
