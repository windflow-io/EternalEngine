package io.windflow.eternalengine.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.windflow.eternalengine.entities.DomainLookup;
import io.windflow.eternalengine.entities.Page;
import io.windflow.eternalengine.error.EternalEngineEditableNotFoundException;
import io.windflow.eternalengine.error.EternalEngineError;
import io.windflow.eternalengine.error.EternalEngineNotFoundException;
import io.windflow.eternalengine.error.EternalEngineWebException;
import io.windflow.eternalengine.persistence.DomainLookupRepository;
import io.windflow.eternalengine.persistence.PageRepository;
import io.windflow.eternalengine.beans.dto.HttpError;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
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

    private final PageRepository pageRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${eternalengine.appDomain}")
    String appDomain;

    @Value(value = "${eternalengine.systemNamespace}")
    String systemNamespace;

    final DomainLookupRepository domainLookupRepository;

    public PageController(@Autowired PageRepository pageRepository, @Autowired DomainLookupRepository domainLookupRepository) {
        this.pageRepository = pageRepository;
        this.domainLookupRepository = domainLookupRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/api/pages/**"}, produces = "application/json")
    @ResponseBody
    public String servePage(HttpServletRequest request, HttpServletResponse response) {

        String siteId = getSiteId(request);
        if (siteId == null) {
            logger.warn("003 Domain does not exist");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            throw new EternalEngineNotFoundException(EternalEngineError.ERROR_003, "domain: " + request.getServerName());
        }

        UrlHelper url = new UrlHelper(request);
        url.setDomain(siteId);

        Optional<Page> optPage = pageRepository.findByDomainAndPath(url.getDomain(), url.getPath());
        if (optPage.isPresent()) { // Page found
            Page page = optPage.get();
            ObjectMapper mapper = new ObjectMapper();

            try {
                JsonNode node = mapper.readTree(page.getJson());
                ((ObjectNode)node).put("siteId", siteId);
                return node.toString();
            } catch (JsonProcessingException ex) {
                throw new EternalEngineWebException(EternalEngineError.ERROR_012, ex.getMessage());
            }

        } else if (pageRepository.existsByDomain(url.getDomain())) { // Domain but no page
            Optional<Page> optNotFound = pageRepository.findByDomainAndType(url.getDomain(), Page.PageType.Page404);
            if (optNotFound.isPresent()) { // Custom 404 for domain
                return optNotFound.get().getJson();
            }
            throw new EternalEngineEditableNotFoundException(EternalEngineError.ERROR_002, "Page not found at " + url.getDomain() + url.getPath(), siteId);

        } else if (pageRepository.existsByType(Page.PageType.PageNormal)) { // domain not found
            throw new EternalEngineEditableNotFoundException(EternalEngineError.ERROR_004, "Domain available for use: " + url.getDomain(), siteId);

        } else if (pageRepository.existsBy()) { // no pages whatsoever
            throw new EternalEngineNotFoundException(EternalEngineError.ERROR_005, "No sites configured");

        } else { // no records found
            throw new EternalEngineNotFoundException(EternalEngineError.ERROR_006, "Database empty");
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = {"/api/pages/**"}, produces = "application/json")
    @ResponseBody
    public String savePage(HttpServletRequest request, HttpServletResponse response, @RequestBody String json) {

        UrlHelper url = new UrlHelper(request);

        Page page;
        Optional<Page> optPage = pageRepository.findByDomainAndPathAndType(url.getDomain(), url.getPath(), Page.PageType.PageNormal);

        if (optPage.isPresent()) {
            page = optPage.get();
        } else {
            page = new Page();
            page.setDomain(url.getDomain());
            page.setPath(url.getPath());
            page.setType(Page.PageType.PageNormal);
            page.setJson(json);
        }

        page.setJson(json);

        return pageRepository.save(page).getJson();
    }

    private String getSiteId(HttpServletRequest request) {
        String requestDomain = request.getServerName();
        if (!requestDomain.endsWith(appDomain)) {
            logger.debug("Domain " + requestDomain + " is not on the app domain " + appDomain);
            Optional<DomainLookup> domainLookup = domainLookupRepository.findFirstByDomainAlias(requestDomain);
            if (domainLookup.isPresent()) {
                logger.debug("Domain " + requestDomain + " maps to " + domainLookup.get().getSiteId());
                return domainLookup.get().getSiteId();
            } else {
                logger.debug("Domain " + requestDomain + " is not configured");
                return null;
            }
        } else {
            logger.debug("Domain " + requestDomain + " is on wildcard app domain " + appDomain);
            return requestDomain;
        }
    }

    @ExceptionHandler({EternalEngineNotFoundException.class, EternalEngineEditableNotFoundException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
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

            if (windEx instanceof EternalEngineEditableNotFoundException) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(json);

                    ((ObjectNode) node).put("siteId", ((EternalEngineEditableNotFoundException)windEx).getSiteId());
                    return new ResponseEntity<String>(node.toString(), headers, HttpStatus.OK);
                } catch (JsonProcessingException ex) {
                    throw new EternalEngineWebException(EternalEngineError.ERROR_012, ex.getMessage());
                }
            }
            return new ResponseEntity<String>(json, headers, HttpStatus.OK);
        }

        if (windEx instanceof EternalEngineEditableNotFoundException) {
            EternalEngineNotFoundException windEx2 = new EternalEngineNotFoundException(EternalEngineError.ERROR_013, "Looking in the " + systemNamespace + " namespace. The original cause of the 404 is " + windEx.getMessage());
            String err = new HttpError(HttpStatus.NOT_FOUND.value(), windEx2.getWindflowError(), windEx2.getDetailOnly(), ((EternalEngineEditableNotFoundException)windEx).getSiteId()).toString();
            return new ResponseEntity<String>(err, headers, HttpStatus.OK);

        }
        EternalEngineNotFoundException windEx2 = new EternalEngineNotFoundException(EternalEngineError.ERROR_013, "Looking in the " + systemNamespace + " namespace. The original cause of the 404 is " + windEx.getMessage());
        String err = new HttpError(HttpStatus.NOT_FOUND.value(), windEx2.getWindflowError(), windEx2.getDetailOnly()).toString();
        return new ResponseEntity<String>(err, headers, HttpStatus.OK);
    }

    @ExceptionHandler(EternalEngineWebException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpError handleEternalEngineWebException(EternalEngineWebException windEx) {
        return new HttpError(HttpStatus.INTERNAL_SERVER_ERROR.value(), windEx.getWindflowError(), windEx.getDetailOnly());
    }

    /*** Helper Class ***/

    public class UrlHelper {

        private HttpServletRequest request;
        private String domain;
        private String path;

        public UrlHelper(HttpServletRequest request) {
            String requestedPath = request.getRequestURI().replace("/api/pages", "").toLowerCase();
            String hostAndPort = requestedPath.split("/")[1];
            String urlPath = requestedPath.replace("/" + hostAndPort + "/", "");
            urlPath = urlPath.endsWith("/") ? urlPath.substring(0, urlPath.length() -1) : urlPath;
            String host = !hostAndPort.contains(":") ? hostAndPort : hostAndPort.substring(0, hostAndPort.indexOf(":"));
            this.domain = host.startsWith("www.") ? host.replace("www.", "") : host;
            this.path = (urlPath.length() == 0 ? "/" : "/" + urlPath);
        }

        public String getDomain() {
            return this.domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getPath() {
            return this.path;
        }

    }


}
