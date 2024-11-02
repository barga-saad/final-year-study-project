package org.example.securityapi.security.config;


import org.example.securityapi.security.filters.TokenValidationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


/**
 * La classe `WebSecurityConfig` configure la sécurité Web de l'application en définissant les filtres et les autorisations.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Value("${authorization.public-endpoints}")
    private String[] publicEndpoints;

    /**
     * Crée un filtre pour la validation des jetons JWT.
     *
     * @return Le filtre de validation des jetons JWT.
     */
    @Bean
    public TokenValidationFilter authenticationJwtTokenFilter() {
        return new TokenValidationFilter();
    }

    /**
     * Configure les règles de sécurité pour les différentes URL de l'application.
     *
     * @param http La configuration de sécurité HTTP.
     * @return Une chaîne de filtres de sécurité.
     * @throws Exception En cas d'erreur lors de la configuration de la sécurité.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().disable(); // Désactive la protection CSRF et la configuration CORS.
        http.authorizeRequests().antMatchers("/public/**", "/v3/api-docs/**").permitAll(); // Autorise l'accès aux URL publiques et à la documentation de l'API.
        http.authorizeRequests().antMatchers(publicEndpoints).permitAll(); // Autorise l'accès aux URL publiques et à la documentation de l'API.
        http.authorizeRequests().anyRequest().authenticated(); // Exige l'authentification pour toutes les autres URL.
        http.sessionManagement().sessionCreationPolicy(STATELESS); // Gère la gestion des sessions (sans état dans ce cas).
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // Ajoute le filtre de validation des jetons JWT avant le filtre d'authentification par nom d'utilisateur et mot de passe.
        return http.build();
    }
}