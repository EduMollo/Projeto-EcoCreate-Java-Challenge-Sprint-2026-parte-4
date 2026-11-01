package br.com.fiap.clyvovet.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.test.util.ReflectionTestUtils;
import javax.sql.DataSource;
import static org.assertj.core.api.Assertions.assertThat;

class SessaoVercelConfigTest {
    @TestConfiguration(proxyBeanMethods = false)
    static class BancoTeste {
        @Bean DataSource dataSource() {
            return new DriverManagerDataSource("jdbc:h2:mem:sessao-config;DB_CLOSE_DELAY=-1", "sa", "");
        }
        @Bean PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test void configuracaoRealAplicaUpsertPostgresAoRepositorioDeSessoes() {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("vercel");
            context.register(BancoTeste.class, SessaoVercelConfig.class);
            context.refresh();
            var repository = context.getBean(JdbcIndexedSessionRepository.class);
            String insert = (String) ReflectionTestUtils.getField(repository, "createSessionAttributeQuery");
            assertThat(insert).contains("clyvovet.spring_session_ATTRIBUTES", "ON CONFLICT", "DO UPDATE SET ATTRIBUTE_BYTES");
            assertThat(context.getBean(org.springframework.session.web.http.CookieSerializer.class))
                .isInstanceOf(org.springframework.session.web.http.DefaultCookieSerializer.class);
        }
    }
}
