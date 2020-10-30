package io.windflow.eternalengine.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.windflow.eternalengine.beans.dto.Token;
import io.windflow.eternalengine.entities.DomainLookup;
import io.windflow.eternalengine.entities.EternalEngineUser;
import io.windflow.eternalengine.entities.Session;
import io.windflow.eternalengine.error.EternalEngineError;
import io.windflow.eternalengine.error.EternalEngineWebException;
import io.windflow.eternalengine.persistence.DomainLookupRepository;
import io.windflow.eternalengine.persistence.SessionRepository;
import io.windflow.eternalengine.persistence.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@PropertySource("classpath:secret.${spring.profiles.active}.properties")
public class AuthService {

    @Value("${eternalengine.encryption.password}")
    String ENCRYPTION_PASSWORD;

    SessionRepository sessionRepository;
    UserRepository userRepository;
    DomainLookupRepository domainLookupRepository;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(@Autowired UserRepository userRepository, @Autowired SessionRepository sessionRepository, @Autowired DomainLookupRepository domainLookupRepository) {
        log.debug("Instantiating AuthService");
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.domainLookupRepository = domainLookupRepository;
    }

    @Transactional
    public EternalEngineUser exchangeToken(String sessionId, String userIp) throws EternalEngineWebException {
        log.debug("Exchange Token Request called with session id " + sessionId + " and user ip address " + userIp);
        log.debug("Looking for a Session in the database with an id of (the session id):" + sessionId);
        Optional<Session> optSession = sessionRepository.findById(UUID.fromString(sessionId));
        if (optSession.isPresent()) {
            log.debug("A matching session was found");
            String sessionIp = optSession.get().getClientIp();
            log.debug("The Session in the database has an ip of " + sessionIp);
            UUID userId = optSession.get().getUserId();
            log.debug("The UserID in the session is " + userId);
            if (sessionIp != null && sessionIp.equals(userIp)) {
                log.debug("UserIp and SessionIp match. Good.");
                Optional<EternalEngineUser> optUser = userRepository.findById(userId);
                log.debug("Fetching user from database with userId " + userId);
                if (optUser.isPresent()) {
                    EternalEngineUser user = optUser.get();
                    log.debug("We have a user! Details are:");
                    //sessionRepository.deleteByUserId(user.getId());
                    System.out.println(user);
                    return user;
                } else {
                    log.debug("There isn't a matching user with that address in the database.");
                    throw new EternalEngineWebException(EternalEngineError.ERROR_010, "No such user");
                }
            } else {
                throw new EternalEngineWebException(EternalEngineError.ERROR_010, "Authorization Failed");
            }
        } else {
            log.debug("A matching session was not found");
            throw new EternalEngineWebException(EternalEngineError.ERROR_010, "Session not found");
        }
    }

    public Token createJWT(EternalEngineUser user) {

        log.debug("Creating JWT with user data");
        log.debug(user.toString());

        Set<DomainLookup> ownedDomains = domainLookupRepository.findByOwnerId(user.getId());

        log.debug("Checking owner domains. This user has " + ownedDomains.size() + " domains.");
        log.debug(ENCRYPTION_PASSWORD.length() > 0 ? "The encryption password (secret) has a length greater than 0. Good." : "Encryption password has - length. Bad.");

        Algorithm algorithm = Algorithm.HMAC256(ENCRYPTION_PASSWORD);
        Map<String, String> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("avatar", user.getAvatarUrl());
        claims.put("userId", user.getId().toString());
        log.debug("Added owned domains will now be added to the JWT claims and listed below:");
        for (DomainLookup domain : ownedDomains) {
            log.debug(domain.getDomainAlias() + " maps to " + domain.getSiteId());
            claims.put(domain.getDomainAlias(), domain.getSiteId());
        }

        log.debug("Attempting to create JWT");
        String jwt = JWT.create().withClaim("user", claims).sign(algorithm);
        log.debug("We have a JWT to return to the client: " + jwt);
        return new Token(jwt);
    }

}
