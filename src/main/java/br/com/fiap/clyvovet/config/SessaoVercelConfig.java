package br.com.fiap.clyvovet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/** Compartilha o login entre instancias e reinicios do container Vercel. */
@Configuration
@Profile("vercel")
@EnableJdbcHttpSession(tableName = "clyvovet.spring_session", maxInactiveIntervalInSeconds = 1800)
public class SessaoVercelConfig {
    @Bean
    CookieSerializer cookieSessao() {
        var cookie = new DefaultCookieSerializer();
        cookie.setCookieName("CLYVO_SESSION");
        cookie.setCookiePath("/");
        cookie.setUseHttpOnlyCookie(true);
        cookie.setUseSecureCookie(true);
        cookie.setSameSite("Lax");
        return cookie;
    }
}
