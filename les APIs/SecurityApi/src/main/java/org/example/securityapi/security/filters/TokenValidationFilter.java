package org.example.securityapi.security.filters;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.bcp.exception.handler.ApiError;
import ma.bcp.security.utils.SecurityTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Ce filtre effectue la validation des jetons de sécurité JWT pour chaque requête entrante
 * et authentifie les utilisateurs en fonction des informations du jeton.
 */
@Slf4j
public class TokenValidationFilter extends OncePerRequestFilter {
    @Autowired
    private SecurityTokenUtils securityTokenUtils;

    /**
     * Cette méthode est appelée pour chaque requête HTTP entrante. Elle extrait le jeton JWT
     * de l'en-tête "Authorization", le valide en utilisant la classe `SecurityTokenUtils`,
     * puis authentifie l'utilisateur en utilisant les rôles extraits du jeton.
     *
     * @param request     La requête HTTP entrante.
     * @param response    La réponse HTTP.
     * @param filterChain La chaîne de filtres.
     * @throws ServletException En cas d'erreur de servlet.
     * @throws IOException      En cas d'erreur d'entrée/sortie.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = securityTokenUtils.extractTokenFromHeader(request);
            if (token != null) {
                DecodedJWT jwt = securityTokenUtils.validateToken(token);
                Authentication authentication = new UsernamePasswordAuthenticationToken(jwt, null, extractRolesFromToken(jwt));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JWTVerificationException | JwkException e) {
            log.error("Token validation failed: " + e.getMessage());
            setErrorResponse(response, "JWT validation Error", e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Cette méthode définit la réponse en cas d'échec de validation du jeton. Elle renvoie une réponse
     * HTTP non autorisée avec un message d'erreur.
     *
     * @param response La réponse HTTP.
     * @param message  Le message d'erreur.
     * @param ex       La description de l'erreur.
     * @throws IOException En cas d'erreur d'entrée/sortie.
     */
    public void setErrorResponse(HttpServletResponse response, String message, String ex) throws IOException {
        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(new ApiError(UNAUTHORIZED, message, ex).convertToJson());
    }

    /**
     * Cette méthode extrait les rôles d'un jeton JWT décodé et les retourne sous forme de
     * collection de `GrantedAuthority`. Les rôles peuvent provenir de l'extension "realm_access"
     * ou être inclus directement dans le jeton JWT.
     *
     * @param decodedJWT Le jeton JWT décodé.
     * @return La collection des autorités accordées.
     */
    public static Collection<? extends GrantedAuthority> extractRolesFromToken(DecodedJWT decodedJWT) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        Claim realmAccessRoles = decodedJWT.getClaim("realm_access");
        Claim jwtRoles = decodedJWT.getClaim("roles");
        if (realmAccessRoles != null && !realmAccessRoles.isNull() && !realmAccessRoles.isMissing()) {
            // Extract keycloak roles
            List<String> roles = parseRolesFromKeycloakToken(realmAccessRoles.toString());
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
            }
        } else if (jwtRoles != null && !jwtRoles.isNull() && !jwtRoles.isMissing()) {
            // Extract roles from JWT Token
            List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role.toUpperCase()));
            }
        }
        return authorities;
    }

    /**
     * Cette méthode analyse les rôles depuis un jeton Keycloak JSON et les retourne sous forme de liste.
     *
     * @param json Le jeton Keycloak JSON.
     * @return La liste des rôles.
     */
    private static List<String> parseRolesFromKeycloakToken(String json) {
        List<String> roles = new ArrayList<>();
        if (json != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(json);
                if (jsonNode != null && jsonNode.has("roles")) {
                    JsonNode rolesNode = jsonNode.get("roles");
                    Iterator<JsonNode> roleIterator = rolesNode.elements();
                    while (roleIterator.hasNext()) {
                        roles.add(roleIterator.next().asText());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return roles;
    }
}