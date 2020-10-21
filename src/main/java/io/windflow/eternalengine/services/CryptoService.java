package io.windflow.eternalengine.services;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

@Service(WebApplicationContext.SCOPE_APPLICATION)
@PropertySource("classpath:secret.${spring.profiles.active}.properties")
public class CryptoService implements InitializingBean {

    private final BasicTextEncryptor textEncryptor = new BasicTextEncryptor();;

    @Value("${eternalengine.encryption.password}")
    String encryptionPassword;


    @PostConstruct
    public void init() {
        textEncryptor.setPassword(encryptionPassword);
    }

    public String encrypt(String plainText) {
        return textEncryptor.encrypt(plainText);
    }

    public String decrypt(String encryptedString) {
        return textEncryptor.decrypt(encryptedString);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        textEncryptor.setPassword(encryptionPassword);
    }
}
