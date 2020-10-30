package io.windflow.eternalengine.services;

import io.windflow.eternalengine.entities.DomainLookup;
import io.windflow.eternalengine.error.EternalEngineError;
import io.windflow.eternalengine.error.EternalEngineNotFoundException;
import io.windflow.eternalengine.persistence.DomainLookupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class DomainFinder {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${eternalengine.appDomain}")
    String appDomain;

    private final DomainLookupRepository domainLookupRepository;

    public DomainFinder(@Autowired DomainLookupRepository domainLookupRepository) {
        this.domainLookupRepository = domainLookupRepository;
    }

    public Optional<DomainLookup> lookup(String domain) {
        return domainLookupRepository.findFirstByDomainAlias(domain);
    }

    public boolean isAppDomain(String host) {
        return host.endsWith(appDomain);
    }

    public String getPath(HttpServletRequest request) {
        String path = request.getRequestURI().replaceFirst("/api/pages", "");
        return path.length() == 0 ? "/" : path;
    }

    public DomainLookup getSite(HttpServletRequest request) throws EternalEngineNotFoundException {
        Optional<DomainLookup> optHost = lookup(request.getServerName());

        if (optHost.isEmpty()) {
            throw new EternalEngineNotFoundException(EternalEngineError.ERROR_003, "domain: " + request.getServerName());
        }

        return optHost.get();
    }

}