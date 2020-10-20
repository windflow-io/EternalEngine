package io.windflow.eternalengine.services;

import io.windflow.eternalengine.entities.EternalEngineUser;
import io.windflow.eternalengine.entities.Session;
import io.windflow.eternalengine.error.WindflowError;
import io.windflow.eternalengine.error.WindflowWebException;
import io.windflow.eternalengine.persistence.SessionRepository;
import io.windflow.eternalengine.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    SessionRepository sessionRepository;
    UserRepository userRepository;

    public AuthService(@Autowired UserRepository userRepository, @Autowired SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EternalEngineUser exchangeToken(String exchangeToken, String userIp) {
        Optional<Session> optSession = sessionRepository.findById(UUID.fromString(exchangeToken));
        if (optSession.isPresent()) {
            String sessionIp = optSession.get().getClientIp();
            UUID userId = optSession.get().getUserId();
            System.out.println("Looking for " + userId);
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
