package tn.dymes.store.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "rsa") // pointer sur applications.proporties les prop qui debut par rsa
public record RsaKeysConfig(RSAPublicKey publicKey, RSAPrivateKey privateKey) {

}
