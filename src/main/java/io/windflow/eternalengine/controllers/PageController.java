package io.windflow.eternalengine.controllers;

import io.windflow.eternalengine.entities.Page;
import io.windflow.eternalengine.error.WindflowError;
import io.windflow.eternalengine.error.WindflowNotFoundException;
import io.windflow.eternalengine.persistence.PageRepository;
import io.windflow.eternalengine.utils.HttpError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RestController
public class PageController {

    private final PageRepository pageRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PageController(@Autowired PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/api/pages/**"}, produces = "application/json")
    @ResponseBody
    public String servePage(HttpServletRequest request, HttpServletResponse response) {

        UrlHelper url = new UrlHelper(request);
        Optional<Page> optPage = pageRepository.findByDomainAndPath(url.getDomain(), url.getPath());
        if (optPage.isPresent()) {
            return optPage.get().getJson();
        } else if (pageRepository.existsByDomain(url.getDomain())) {
            Optional<Page> optNotFound = pageRepository.findByDomainAndType(url.getDomain(), Page.PageType.Page404);
            if (optNotFound.isPresent()) {
                return optNotFound.get().getJson();
            }
            logger.warn("002 Page does not exist");
            throw new WindflowNotFoundException(WindflowError.ERROR_002, "domain: " + url.getDomain() + " and path: " + url.getPath());
        } else if (pageRepository.existsByType(Page.PageType.PageNormal)) {
            logger.warn("003 Domain does not exist");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            throw new WindflowNotFoundException(WindflowError.ERROR_003, "domain: " + url.getDomain());
        } else if (pageRepository.existsBy()) {
            logger.warn("004 No sites configured");
            throw new WindflowNotFoundException(WindflowError.ERROR_004);
        } else {
            logger.warn("005 Database is empty");
            throw new WindflowNotFoundException(WindflowError.ERROR_005);
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

    /**@TODO Common Errors must be moved to a common error handling class **/

    @ExceptionHandler(WindflowNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpError handleWindflowNotFoundException(WindflowNotFoundException windEx) {
        return new HttpError(HttpStatus.NOT_FOUND.value(), windEx.getWindflowError(), windEx.getDetailOnly());
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

        public String getPath() {
            return this.path;
        }

    }


}
