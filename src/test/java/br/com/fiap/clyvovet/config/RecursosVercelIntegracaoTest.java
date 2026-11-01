package br.com.fiap.clyvovet.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-vercel.properties")
class RecursosVercelIntegracaoTest {
    @Autowired MockMvc mvc;

    @Test void recursosPublicosTemCacheSemNecessitarLogin() throws Exception {
        for (String path : new String[]{"/css/app.css", "/js/tema.js", "/js/navegacao.js"}) {
            String cache = mvc.perform(get(path)).andExpect(status().isOk())
                .andReturn().getResponse().getHeader("Cache-Control");
            assertThat(cache).contains("max-age=86400", "public");
        }
    }

    @Test void loginUsaUrlsVersionadasEContinuaSemCachePublico() throws Exception {
        var response = mvc.perform(get("/login")).andExpect(status().isOk()).andReturn().getResponse();
        assertThat(response.getContentAsString()).containsPattern("/css/app-[a-f0-9]{32}\\.css")
            .containsPattern("/js/tema-[a-f0-9]{32}\\.js");
        assertThat(response.getHeader("Cache-Control")).contains("no-store");
    }
}
