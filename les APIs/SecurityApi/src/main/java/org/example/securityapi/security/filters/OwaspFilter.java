package org.example.securityapi.security.filters;

import org.example.securityapi.security.utils.OwaspRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Open Web Application Security Project Filter
/*
* OwaspFilter est un filtre de sécurité qui ajoute des en-têtes HTTP de sécurité conformes aux recommandations de l'OWASP
* à chaque requête HTTP traitée par l'application. Ces en-têtes renforcent la sécurité de l'application en protégeant
* contre diverses vulnérabilités web courantes.
* */
public class OwaspFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.addOwaspSecurityHeaders(response);
        chain.doFilter(new OwaspRequestWrapper((HttpServletRequest) request), response);
    }

    private void addOwaspSecurityHeaders(ServletResponse response) {
        HttpServletResponse res = (HttpServletResponse) response;
        // enforce the use of https and max age of 1 year
        res.addHeader("STRICT-TRANSPORT-SECURITY", "max-age=31536000; includeSubDomains");
        // against clickjacking attacks by specifying who can embed the page in an iframe "SAMEORIGIN" means the
        // page can only be embedded in frames on the same origin
        res.addHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
        // X-XSS-Protection (Cross-site Scripting)
        res.addHeader("X-XSS-PROTECTION", "1; mode=block");
        // This header prevents browsers from interpreting files as something else than declared by the content
        // type in the HTTP headers, helping to mitigate MIME sniffing attacks
        res.addHeader("X-CONTENT-TYPE-OPTIONS", "nosniff");
    }

}