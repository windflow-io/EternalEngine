package io.windflow.eternalengine.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.windflow.eternalengine.beans.dto.Token;
import io.windflow.eternalengine.entities.EternalEngineUser;
import io.windflow.eternalengine.entities.Session;
import io.windflow.eternalengine.error.WindflowError;
import io.windflow.eternalengine.error.WindflowWebException;
import io.windflow.eternalengine.persistence.SessionRepository;
import io.windflow.eternalengine.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@PropertySource("classpath:secret.properties")
public class AuthService {

    @Value("${io.windflow.encryption.password}")
    String ENCRYPTION_PASSWORD;

    SessionRepository sessionRepository;
    UserRepository userRepository;

    public AuthService(@Autowired UserRepository userRepository, @Autowired SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    public Token createJWT(EternalEngineUser user) {
        Algorithm algorithm = Algorithm.HMAC256(ENCRYPTION_PASSWORD);
        Map<String, String> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("avatar", user.getAvatarUrl());
        String jwt = JWT.create().withClaim("user", claims).sign(algorithm);
        return new Token(jwt);
    }

    @Transactional
    public EternalEngineUser exchangeToken(String sessionId, String userIp) {
        Optional<Session> optSession = sessionRepository.findById(UUID.fromString(sessionId));
        if (optSession.isPresent()) {
            String sessionIp = optSession.get().getClientIp();
            UUID userId = optSession.get().getUserId();
            if (sessionIp != null && sessionIp.equals(userIp)) {
                Optional<EternalEngineUser> optUser = userRepository.findById(userId);
                if (optUser.isPresent()) {
                    EternalEngineUser user = optUser.get();
                    sessionRepository.deleteByUserId(user.getId());
                    return user;
                } else {
                    throw new WindflowWebException(WindflowError.ERROR_009, "No such user");
                }
            } else {
                throw new WindflowWebException(WindflowError.ERROR_009, "Authorization Failed");
            }
        } else {
            throw new WindflowWebException(WindflowError.ERROR_009, "Session not found");
        }
    }
}
