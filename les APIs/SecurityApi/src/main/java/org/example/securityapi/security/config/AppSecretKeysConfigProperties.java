package org.example.securityapi.security.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe `AppSecretKeysConfigProperties` est une classe de configuration utilisée pour charger les clés secrètes des applications.
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "app.secret.keys")
public class AppSecretKeysConfigProperties {
    /**
     * Une liste des clés secrètes des applications.
     */
    private final List<AppSecretKeys> apps = new ArrayList<>();

    /**
     * Une classe interne représentant une paire nom/clé secrète pour une application.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AppSecretKeys {
        /**
         * Le nom de l'application.
         */
        private String name;

        /**
         * La clé secrète associée à l'application.
         */
        private String secretKey;
    }
}