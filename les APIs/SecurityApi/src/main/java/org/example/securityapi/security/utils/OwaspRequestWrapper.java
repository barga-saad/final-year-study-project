package org.example.securityapi.security.utils;
import com.google.common.io.CharStreams;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * @author bilalslayki
 */
public class OwaspRequestWrapper extends HttpServletRequestWrapper {

    private String _body;

    public OwaspRequestWrapper(HttpServletRequest servletRequest) throws IOException {
        super(servletRequest);
        this._body = CharStreams.toString(servletRequest.getReader());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this._body.getBytes());
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener arg0) {
                // TODO Auto-generated method stub
            }
        };

    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return new String[0];
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = this.stripMaliciousContent(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return this.stripMaliciousContent(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return this.stripMaliciousContent(value);
    }

    private String stripMaliciousContent(String value) {
        String cleanValue = this.stripXssInjection(value);
        cleanValue = this.stripSqlInjection(cleanValue);
        return cleanValue;
    }

    private String stripXssInjection(String value) {
        if (value != null) {
            // Eviter les caractères null
            value = value.replaceAll("", "");
            // Eviter tout ce qui est entre les balises <scripts>
            value = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE).matcher(value).replaceAll("");
            // Eviter tout ce qui est à l'intérieur de src='...'
            value = Pattern
                    .compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'",
                            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
                    .matcher(value).replaceAll("");
            value = Pattern
                    .compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"",
                            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
                    .matcher(value).replaceAll("");
            // Supprimer tout tag </script> qui traine
            value = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE).matcher(value).replaceAll("");
            // Supprimer tout tag <script ...> qui traine
            value = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
                    .matcher(value).replaceAll("");
            // Eviter les expressions "eval(...)"
            value = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
                    .matcher(value).replaceAll("");
            // Eviter les expression "expression(...)"
            value = Pattern
                    .compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
                    .matcher(value).replaceAll("");
            // Eviter les expressions "javascript:..."
            value = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE).matcher(value).replaceAll("");
            // Eviter les expressions "vbscript:..."
            value = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE).matcher(value).replaceAll("");
            // Eviter les expressions "onload="
            value = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
                    .matcher(value).replaceAll("");
        }
        return value;
    }

    private String stripSqlInjection(String value) {
        if (value != null) {
            value = value.replaceAll("", "");
            value = value.replaceAll(";", "");
            value = value.replaceAll("[']+", "''");
            value = Pattern.compile(Pattern.quote("AND"), Pattern.CASE_INSENSITIVE).matcher(value).replaceAll("");
            value = Pattern.compile(Pattern.quote("OR"), Pattern.CASE_INSENSITIVE).matcher(value).replaceAll("");
            value = Pattern.compile(Pattern.quote("JOIN"), Pattern.CASE_INSENSITIVE).matcher(value).replaceAll("");
            value = Pattern.compile(Pattern.quote("SELECT"), Pattern.CASE_INSENSITIVE).matcher(value).replaceAll("");
            value = Pattern.compile(Pattern.quote("ROWNUM"), Pattern.CASE_INSENSITIVE).matcher(value).replaceAll("");
            value = Pattern.compile(Pattern.quote("UPDATE"), Pattern.CASE_INSENSITIVE).matcher(value).replaceAll("");
            value = Pattern.compile(Pattern.quote("INSERT"), Pattern.CASE_INSENSITIVE).matcher(value).replaceAll("");
            value = Pattern.compile(Pattern.quote("DELETE"), Pattern.CASE_INSENSITIVE).matcher(value).replaceAll("");
        }
        return value;
    }
}