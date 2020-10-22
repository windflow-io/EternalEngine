package io.windflow.eternalengine.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.windflow.eternalengine.beans.PageData;
import io.windflow.eternalengine.entities.DomainLookup;
import io.windflow.eternalengine.entities.Page;
import io.windflow.eternalengine.error.EternalEngineError;
import io.windflow.eternalengine.persistence.DomainLookupRepository;
import io.windflow.eternalengine.persistence.PageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
@PropertySource("eternalengine.${spring.profiles.active}.properties")
public class WebController {

    final PageRepository pageRepository;
    final DomainLookupRepository domainLookupRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${eternalengine.cdn:undefined}")
    String cdn;

    public WebController(@Autowired PageRepository pageRepository, @Autowired DomainLookupRepository domainLookupRepository, @Autowired DomainLookupRepository domainLookupRepository1) {
        this.pageRepository = pageRepository;
        this.domainLookupRepository = domainLookupRepository1;
    }

    @GetMapping(value = {"/**/{regex:[-a-zA-Z0-9]*}", "/"})
    public String spa(HttpServletRequest request, HttpServletResponse response, Model model) throws JsonProcessingException {

        String requestDomain = request.getServerName();

        Optional<DomainLookup> domainLookup = domainLookupRepository.findFirstByDomainAlias(requestDomain);
        if (domainLookup.isPresent()) requestDomain = domainLookup.get().getSiteId();

        /** Look for the page **/
        Optional<Page> optPage = pageRepository.findByDomainAndPath(requestDomain, request.getServletPath());
        if (optPage.isPresent()) {
            PageData pageData = prepareModel(optPage.get());
            model.addAttribute("pageData", pageData);
            response.setStatus(HttpServletResponse.SC_OK);
            return "spa200";
        }

        /** Look for the error 404 **/
        Optional<Page> optCustom404 = pageRepository.findByDomainAndType(requestDomain, Page.PageType.Page404);
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
        return new ObjectMapper().readValue(page.getJson(), PageData.class);
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(JsonProcessingException ex, HttpServletRequest request, HttpServletResponse response) {
        String domainAndPath = "domain:" + request.getServerName() + " and path:" + request.getServletPath();
        logger.error(EternalEngineError.ERROR_007 + ": " + domainAndPath + " " + ex.getMessage());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ex.printStackTrace();
        return "spaError";
    }
}
