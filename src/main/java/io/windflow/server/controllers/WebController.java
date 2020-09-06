package io.windflow.server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.windflow.server.beans.PageData;
import io.windflow.server.entities.Page;
import io.windflow.server.error.WindflowError;
import io.windflow.server.error.WindflowWebException;
import io.windflow.server.persistence.PageRepository;
import io.windflow.server.utils.HttpError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class WebController {

    PageRepository pageRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${io.windflow.cdn:undefined}")
    String cdn;

    public WebController(@Autowired PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @GetMapping(value = {"/**/{regex:[-a-zA-Z0-9]*}", "/"})
    public String spa(HttpServletRequest request, HttpServletResponse response, Model model) throws JsonProcessingException {

        /** @TODO: Send PageData down with Index **/

        /** Look for the page **/
        Optional<Page> optPage = pageRepository.findByDomainAndPath(request.getServerName(), request.getServletPath());
        if (optPage.isPresent()) {
            PageData pageData = prepareModel(optPage.get());
            model.addAttribute("pageData", pageData);
            response.setStatus(HttpServletResponse.SC_OK);
            return "spa200";
        }

        /** Look for the error 404 **/
        Optional<Page> optCustom404 = pageRepository.findByDomainAndType(request.getServerName(), Page.PageType.Page404);
        if (optCustom404.isPresent()) {
            PageData pageData = prepareModel(optCustom404.get());
            model.addAttribute("pageData", pageData);
            response.setStatus(HttpServletResponse.SC_OK);
            return "spa200";
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return "spaError";

    }

    /*** Private Methods ***/

    private PageData prepareModel(Page page) throws JsonProcessingException {
        PageData pageData = new ObjectMapper().readValue(page.getJson(), PageData.class);
        pageData.setCdn(this.cdn);
        return pageData;
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(JsonProcessingException ex, HttpServletRequest request, HttpServletResponse response) {
        String domainAndPath = "domain:" + request.getServerName() + " and path:" + request.getServletPath();
        logger.error("Error interpreting page data: " + domainAndPath + " " + ex.getMessage());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ex.printStackTrace();
        return "spaError";
    }
}
