package io.windflow.eternalengine.controllers;

import io.windflow.eternalengine.entities.DomainLookup;
import io.windflow.eternalengine.entities.Page;
import io.windflow.eternalengine.error.EternalEngineError;
import io.windflow.eternalengine.error.EternalEngineNotFoundException;
import io.windflow.eternalengine.error.EternalEngineWebException;
import io.windflow.eternalengine.persistence.DomainLookupRepository;
import io.windflow.eternalengine.persistence.PageRepository;
import io.windflow.eternalengine.beans.dto.HttpError;
import io.windflow.eternalengine.services.DomainFinder;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
public class PageController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PageRepository pageRepository;
    private final DomainFinder domainFinder;

    @Value(value = "${eternalengine.systemNamespace}")
    String systemNamespace;

    public PageController(@Autowired PageRepository pageRepository, @Autowired DomainLookupRepository domainLookupRepository, @Autowired DomainFinder domainFinder) {
        this.pageRepository = pageRepository;
        this.domainFinder = domainFinder;
    }


    @RequestMapping(method = RequestMethod.GET, value = {"/api/pages/**", "/api/pages"}, produces = "application/json")
    @ResponseBody
    public String servePage(HttpServletRequest request, HttpServletResponse response) {

        DomainLookup site = domainFinder.getSite(request);
        String path = domainFinder.getPath(request);
        String siteId = site.getSiteId();

        // @TODO: PERMISSIONS IN HERE

        Optional<Page> optPage = pageRepository.findByDomainAndPath(siteId, path);
        if (optPage.isPresent()) { // Page found
            return optPage.get().getJson();

        } else if (pageRepository.existsByDomain(siteId)) { // Domain but no page
            Optional<Page> optNotFound = pageRepository.findByDomainAndType(siteId, Page.PageType.Page404);
            if (optNotFound.isPresent()) { // Custom 404 for domain
                return optNotFound.get().getJson();
            }
            throw new EternalEngineNotFoundException(EternalEngineError.ERROR_002, "Page not found at " + request.getServerName() + path);

        } else if (pageRepository.existsByType(Page.PageType.PageNormal)) { // domain not found
            throw new EternalEngineNotFoundException(EternalEngineError.ERROR_004, "Domain available for use: " + request.getServerName());

        } else if (pageRepository.existsBy()) { // no pages whatsoever
            throw new EternalEngineNotFoundException(EternalEngineError.ERROR_005, "No sites configured");

        } else { // no records found
            throw new EternalEngineNotFoundException(EternalEngineError.ERROR_006, "Database empty");
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = {"/api/pages/**"}, produces = "application/json")
    @ResponseBody
    public String savePage(HttpServletRequest request, @RequestBody String json) {

        DomainLookup site = domainFinder.getSite(request);
        String path = domainFinder.getPath(request);
        String siteId = site.getSiteId();

        Page page;
        Optional<Page> optPage = pageRepository.findByDomainAndPathAndType(siteId, path, Page.PageType.PageNormal);

        if (optPage.isPresent()) {
            page = optPage.get();
        } else {
            page = new Page();
            page.setDomain(siteId);
            page.setPath(path);
            page.setType(Page.PageType.PageNormal);
            page.setJson(json);
        }

        page.setJson(json);

        return pageRepository.save(page).getJson();
    }



    @ExceptionHandler({EternalEngineNotFoundException.class})
    public ResponseEntity<String> handleEternalEngineNotFoundException(EternalEngineNotFoundException windEx) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Optional<Page> optNotFound = pageRepository.findByDomainAndType(systemNamespace, Page.PageType.Page404);
        if (optNotFound.isPresent()) { // General 404 for domain
            String json = optNotFound.get().getJson();

            Map<String, String> dynamicPageVariables = new HashMap<>();
            dynamicPageVariables.put("errorTitle", "404");
            dynamicPageVariables.put("errorDescription", windEx.getErrorDetail());
            dynamicPageVariables.put("errorDetail", windEx.getWindflowError().getDescription());

            StringSubstitutor replacer = new StringSubstitutor(dynamicPageVariables);
            json = replacer.replace(json);

            return new ResponseEntity<>(json, headers, HttpStatus.OK);
        }

        EternalEngineNotFoundException windEx2 = new EternalEngineNotFoundException(EternalEngineError.ERROR_013, "Looking in the " + systemNamespace + " namespace. The original cause of the 404 is " + windEx.getMessage());
        String err = new HttpError(HttpStatus.NOT_FOUND.value(), windEx2.getWindflowError(), windEx2.getDetailOnly()).toString();
        return new ResponseEntity<>(err, headers, HttpStatus.OK);
    }

    @ExceptionHandler(EternalEngineWebException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpError handleEternalEngineWebException(EternalEngineWebException windEx) {
        return new HttpError(HttpStatus.INTERNAL_SERVER_ERROR.value(), windEx.getWindflowError(), windEx.getDetailOnly());
    }

}
