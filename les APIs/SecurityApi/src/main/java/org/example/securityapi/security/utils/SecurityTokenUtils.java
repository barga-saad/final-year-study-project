package org.example.securityapi.security.utils;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import ma.bcp.exception.custom.exceptions.UnTrustedIssuer;
import ma.bcp.security.config.AppSecretKeysConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Optional;

/**
 * La classe `SecurityTokenUtils` fournit des méthodes pour extraire, valider et gérer les jetons de sécurité en fonction de
 * l'émetteur (issuer) et en utilisant les clés appropriées pour la validation des jetons JWT.
 */
@Slf4j
@Component
public class SecurityTokenUtils {

    @Value("${keycloak.allowedIssuers}")
    private String[] keycloakAllowedIssuers;
    @Autowired
    private AppSecretKeysConfigProperties appSecretKeysConfigProperties;

    /**
     * Cette méthode extrait le jeton d'authentification de l'en-tête "Authorization" d'une requête HTTP. Si le jeton
     * est présent et correctement formatté, il est renvoyé.
     *
     * @param request La requête HTTP entrante.
     * @return Le jeton JWT extrait, ou null s'il n'est pas présent ou mal formatté.
     */
    public String extractTokenFromHeader(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        return StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ") ? headerAuth.substring(7) : null;
    }

    /**
     * Cette méthode construit l'URL du certificat Keycloak en utilisant l'ISS (émetteur) du jeton.
     *
     * @param token Le jeton JWT contenant l'ISS.
     * @return L'URL du certificat Keycloak.
     */
    private String getKeycloakCertificateUrl(DecodedJWT token) {
        return token.getIssuer() + "/protocol/openid-connect/certs";
    }

    /**
     * Cette méthode charge la clé publique correspondant au jeton JWT en utilisant le protocole JWK (JSON Web Key).
     *
     * @param token Le jeton JWT contenant l'ID de la clé.
     * @return La clé publique chargée.
     * @throws JwkException Si une erreur survient lors de la récupération de la clé publique.
     */
    private RSAPublicKey loadPublicKey(DecodedJWT token) throws JwkException {
        final String url = getKeycloakCertificateUrl(token);
        try {
            JwkProvider provider = new UrlJwkProvider(new URL(url));
            Jwk jwk = provider.get(token.getKeyId());
            if (jwk != null) {
                return (RSAPublicKey) jwk.getPublicKey();
            } else {
                throw new JwkException("JWK with the specified key ID not found.");
            }
        } catch (IOException e) {
            throw new JwkException("Failed to fetch JWK from the URL: " + url, e);
        }
    }

    /**
     * Cette méthode valide un jeton JWT. Elle le décode, choisit l'algorithme de validation approprié en fonction
     * de l'émetteur du jeton, construit un vérificateur JWT, puis vérifie le jeton. Si le jeton est valide, il est renvoyé.
     *
     * @param token Le jeton JWT à valider.
     * @return Le jeton JWT validé sous forme de DecodedJWT.
     * @throws JWTVerificationException En cas d'erreur de validation du jeton.
     * @throws JWTCreationException     En cas d'erreur de création du vérificateur JWT.
     * @throws JwkException             En cas d'erreur liée à la récupération de clés JWK.
     */
    public DecodedJWT validateToken(String token) throws JWTVerificationException, JWTCreationException, JwkException {
        DecodedJWT jwt = JWT.decode(token);
        Algorithm algorithm = chooseAlgorithmForToken(jwt);
        JWTVerifier verifier = buildVerifier(algorithm, jwt);
        return verifier.verify(token);
    }

    /**
     * Cette méthode choisit l'algorithme de validation approprié en fonction de l'émetteur du jeton.
     *
     * @param jwt Le jeton JWT à valider.
     * @return L'algorithme de validation choisi.
     * @throws JwkException Si une erreur survient lors de la récupération de la clé publique.
     */
    private Algorithm chooseAlgorithmForToken(DecodedJWT jwt) throws JwkException {
        if (isKeycloakIssuer(keycloakAllowedIssuers, jwt.getIssuer())) {
            RSAPublicKey publicKey = loadPublicKey(jwt);
            return Algorithm.RSA256(publicKey, null);
        } else if (isTrustedIssuer(jwt.getIssuer())) {
            return Algorithm.HMAC256(getAssociatedSecretKey(jwt.getIssuer()));
        } else {
            throw new UnTrustedIssuer("JWT validation failed: Issuer is not trusted!");
        }
    }

    /**
     * Cette méthode construit un vérificateur JWT en utilisant l'algorithme de validation et l'émetteur du jeton.
     *
     * @param algorithm L'algorithme de validation à utiliser.
     * @param jwt       Le jeton JWT à valider.
     * @return Le vérificateur JWT construit.
     */
    private JWTVerifier buildVerifier(Algorithm algorithm, DecodedJWT jwt) {
        return JWT.require(algorithm).withIssuer(jwt.getIssuer()).build();
    }

    /**
     * Cette méthode vérifie si l'émetteur (issuer) du jeton est autorisé en comparant avec une liste d'émetteurs autorisés,
     * principalement des émetteurs Keycloak.
     *
     * @param keycloakAllowedIssuers La liste des émetteurs autorisés.
     * @param possibleIssuer         L'émetteur à vérifier.
     * @return true si l'émetteur est autorisé, sinon false.
     */
    private boolean isKeycloakIssuer(String[] keycloakAllowedIssuers, String possibleIssuer) {
        return Arrays.asList(keycloakAllowedIssuers).contains(possibleIssuer);
    }

    /**
     * Cette méthode vérifie si l'émetteur (issuer) du jeton est digne de confiance en le comparant avec une liste d'émetteurs
     * de confiance provenant de la configuration.
     *
     * @param possibleIssuer L'émetteur à vérifier.
     * @return true si l'émetteur est digne de confiance, sinon false.
     */
    private boolean isTrustedIssuer(String possibleIssuer) {
        return appSecretKeysConfigProperties.getApps().stream().anyMatch(app -> app.getName().equals(possibleIssuer));
    }

    /**
     * Cette méthode récupère la clé secrète associée à un émetteur spécifique à partir de la configuration. Elle renvoie
     * la clé secrète correspondante s'il y en a une.
     *
     * @param possibleIssuer L'émetteur pour lequel récupérer la clé secrète.
     * @return La clé secrète associée à l'émetteur, ou null si aucune clé n'est associée.
     */
    private String getAssociatedSecretKey(String possibleIssuer) {
        Optional<AppSecretKeysConfigProperties.AppSecretKeys> app = appSecretKeysConfigProperties.getApps()
                .stream()
                .filter(x -> x.getName().equals(possibleIssuer))
                .findFirst();
        return app.map(AppSecretKeysConfigProperties.AppSecretKeys::getSecretKey).orElse(null);
    }

}