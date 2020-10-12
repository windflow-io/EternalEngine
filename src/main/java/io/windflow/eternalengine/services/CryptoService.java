package io.windflow.eternalengine.services;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

@Service(WebApplicationContext.SCOPE_APPLICATION)
@PropertySource("classpath:secret.properties")
public class CryptoService implements InitializingBean {

    private final BasicTextEncryptor textEncryptor = new BasicTextEncryptor();;

    @Value("${io.windflow.encryption.password}")
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
