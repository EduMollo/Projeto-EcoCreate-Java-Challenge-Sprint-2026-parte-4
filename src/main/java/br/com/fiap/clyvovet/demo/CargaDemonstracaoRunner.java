package br.com.fiap.clyvovet.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("postgres")
@ConditionalOnProperty(name = "clyvovet.demo.enabled", havingValue = "true")
public class CargaDemonstracaoRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(CargaDemonstracaoRunner.class);
    private final CargaDemonstracaoService carga;

    public CargaDemonstracaoRunner(CargaDemonstracaoService carga) { this.carga = carga; }

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("CARGA_DEMO: {}", carga.carregar());
        } catch (RuntimeException erro) {
            // A transacao do servico reverte antes deste ponto; a aplicacao continua disponivel.
            log.error("CARGA_DEMO: falha, insercoes revertidas ({})", erro.getClass().getSimpleName());
        }
    }
}
