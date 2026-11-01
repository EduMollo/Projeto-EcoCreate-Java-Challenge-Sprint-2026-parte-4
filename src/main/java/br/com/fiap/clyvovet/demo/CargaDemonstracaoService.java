package br.com.fiap.clyvovet.demo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Locale;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.clyvovet.model.enums.Especie;
import br.com.fiap.clyvovet.model.enums.TipoConsulta;
import br.com.fiap.clyvovet.model.enums.TipoEvento;

/** Carga opt-in, identificada e atomica; nao modifica cadastros nem credenciais existentes. */
@Service
public class CargaDemonstracaoService {
    private static final String MARCADOR = "demo.s4.clinica.1@example.invalid";
    private final JdbcTemplate jdbc;

    public CargaDemonstracaoService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record Resumo(boolean criada, long clinicas, long tutores, long pets,
            long consultas, long vacinas, long medicamentos, long eventos) {}

    @Transactional
    public Resumo carregar() {
        // Serializa cargas simultaneas de containers, na mesma transacao das insercoes.
        boolean postgres = Boolean.TRUE.equals(jdbc.execute((ConnectionCallback<Boolean>)
                c -> c.getMetaData().getDatabaseProductName().equals("PostgreSQL")));
        if (postgres) jdbc.execute("select pg_advisory_xact_lock(4284, 1)");
        if (jdbc.queryForObject("select count(*) from tb_clinica where email = ?", Long.class, MARCADOR) > 0) {
            return resumir(false);
        }
        LocalDate hoje = LocalDate.now();
        String[] cidades = {"Sao Paulo", "Campinas", "Santos", "Curitiba", "Rio de Janeiro", "Sorocaba"};
        String[] estados = {"SP", "SP", "SP", "PR", "RJ", "SP"};
        long[] clinicas = new long[6];
        for (int i = 0; i < clinicas.length; i++) {
            clinicas[i] = inserir("tb_clinica", "nome,cnpj,telefone,email,endereco,cidade,estado,ativo",
                    "[DEMO] Clinica " + (i + 1) + " - " + cidades[i],
                    cnpj(String.format(Locale.ROOT, "980000%02d0001", i + 1)),
                    String.format(Locale.ROOT, "11000000%03d", i + 1),
                    "demo.s4.clinica." + (i + 1) + "@example.invalid",
                    "Rua Ficticia, " + (i + 1) + " - Dados de demonstracao",
                    cidades[i], estados[i], i < 5);
        }
        long[] tutores = new long[32];
        for (int i = 0; i < tutores.length; i++) {
            tutores[i] = inserir("tb_tutor", "nome,cpf,email,telefone,data_nascimento,ativo",
                    String.format(Locale.ROOT, "[DEMO] Tutor %02d%s", i + 1, i >= 30 ? " - sem pets" : ""),
                    cpf(String.format(Locale.ROOT, "980000%03d", i + 1)),
                    "demo.s4.tutor." + (i + 1) + "@example.invalid",
                    String.format(Locale.ROOT, "11000001%03d", i + 1),
                    hoje.minusYears(22 + i % 40).minusDays(i), i != 31);
        }
        String[] nomes = {"Luna", "Thor", "Mel", "Nina", "Bento", "Simba"};
        String[] cenarios = {"em dia", "alerta alto", "primeira consulta", "alerta medio", "acompanhamento", "inativo"};
        for (int i = 0; i < 60; i++) {
            int caso = i % 6;
            long clinica = clinicas[i % 5];
            LocalDate nascimento = caso == 2 ? hoje.minusMonths(6)
                    : hoje.minusYears(caso == 1 ? 12 : caso == 3 ? 8 : 3 + i % 3);
            long pet = inserir("tb_pet",
                    "nome,especie,raca,data_nascimento,peso_kg,sexo,castrado,microchip,observacoes,ativo,id_tutor,id_clinica_principal",
                    String.format(Locale.ROOT, "[DEMO] %s %02d - %s", nomes[caso], i + 1, cenarios[caso]),
                    Especie.values()[(i / 6) % Especie.values().length].name(), "Raca de demonstracao",
                    nascimento, BigDecimal.valueOf(100 + i * 35L, 2), i % 2 == 0 ? "FEMEA" : "MACHO",
                    i % 3 != 0, String.format(Locale.ROOT, "DEMO-S4-%03d", i + 1),
                    "Dado ficticio para testes. Cenario: " + cenarios[caso] + ". Sem orientacao clinica real.",
                    caso != 5, tutores[i / 2], caso == 5 ? null : clinica);
            if (caso == 5) continue; // Pet sem dependencias para experimentar reativacao e edicao.
            String veterinario = "[DEMO] Veterinario " + (1 + i % 3);
            Long ultima = null;
            LocalDate dataUltima = null;
            if (caso != 2) {
                dataUltima = hoje.minusDays(caso == 1 ? 600 : caso == 3 ? 210 : caso == 4 ? 60 : 30);
                for (int h = 1; h >= 0; h--) {
                    LocalDate data = dataUltima.minusDays(h * 365L);
                    Long consulta = consulta(pet, clinica, data, "CONCLUIDA",
                            TipoConsulta.values()[(i + h) % TipoConsulta.values().length], veterinario,
                            i % 8 == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(10000 + i * 113L + h * 500, 2),
                            caso == 0 && h == 0 ? hoje.plusDays(7) : null, null);
                    evento(pet, consulta, TipoEvento.CONSULTA, "Atendimento ficticio concluido.", data, null);
                    if (h == 0) ultima = consulta;
                }
                consulta(pet, clinica, hoje.minusDays(5), "CANCELADA", TipoConsulta.ROTINA,
                        veterinario, null, null, "Cancelamento ficticio a pedido do tutor.");
            }
            LocalDate futura = caso == 0 ? hoje.plusDays(7) : hoje.plusDays(i % 8);
            consulta(pet, clinica, futura, "AGENDADA",
                    caso == 0 ? TipoConsulta.RETORNO : TipoConsulta.values()[i % TipoConsulta.values().length],
                    veterinario, null, null, null);
            if (caso == 0) evento(pet, ultima, TipoEvento.RETORNO,
                    "Retorno ficticio vinculado ao atendimento concluido.", dataUltima, futura);
            if (caso == 1) consulta(pet, clinica, hoje.minusDays(10), "AGENDADA",
                    TipoConsulta.PREVENTIVA, veterinario, null, null, null);
            Long andamento = caso == 4 ? consulta(pet, clinica, hoje, "EM_ANDAMENTO",
                    TipoConsulta.EXAME, veterinario, null, null, null) : null;
            if (ultima != null) {
                for (int v = 0; v < 3; v++) {
                    boolean vencida = caso == 1 || caso == 3 && v < 2;
                    LocalDate aplicacao = dataUltima;
                    LocalDate validade = vencida ? hoje.minusDays(10 + v * 10L) : hoje.plusDays(300 + v);
                    LocalDate dose = caso == 1 || caso == 3 && v == 0
                            ? hoje.minusDays(5 + v) : hoje.plusDays(90 + v);
                    inserir("tb_vacina",
                            "nome,fabricante,lote,data_aplicacao,data_validade,proxima_dose,criado_em,id_pet,id_clinica",
                            "[DEMO] Vacina " + (v + 1), "Fabricante ficticio", "DEMO-S4-" + i + "-" + v,
                            aplicacao, validade, dose, aplicacao.atTime(9, 0), pet, clinica);
                    evento(pet, ultima, TipoEvento.VACINA, "Vacina ficticia registrada.", aplicacao, dose);
                }
                boolean continuo = caso == 3 || caso == 4;
                medicamento(pet, ultima, dataUltima, continuo ? null : dataUltima.plusDays(7), continuo);
                if (andamento != null) medicamento(pet, andamento, hoje, hoje.plusDays(7), false);
            }
            evento(pet, null, i % 3 == 0 ? TipoEvento.CHECKUP : i % 3 == 1
                    ? TipoEvento.VERMIFUGACAO : TipoEvento.OBSERVACAO,
                    "Registro ficticio para testar a linha do tempo.", hoje.minusDays(1), hoje.plusDays(15));
        }
        return resumir(true);
    }

    private Long consulta(long pet, long clinica, LocalDate data, String status, TipoConsulta tipo,
            String veterinario, BigDecimal valor, LocalDate retorno, String motivo) {
        return inserir("tb_consulta",
                "data_consulta,status,tipo_consulta,veterinario,diagnostico,prescricao,observacoes,valor,data_retorno,motivo_cancelamento,criado_em,id_pet,id_clinica",
                data, status, tipo.name(), veterinario,
                status.equals("CONCLUIDA") ? "Diagnostico ficticio para demonstracao do sistema." : null,
                status.equals("CONCLUIDA") ? "Prescricao simulada; sem indicacao de uso real." : null,
                "[DEMO] Registro de teste.", valor, retorno, motivo, data.atTime(8, 0), pet, clinica);
    }

    private void medicamento(long pet, long consulta, LocalDate inicio, LocalDate fim, boolean continuo) {
        inserir("tb_medicamento",
                "nome,principio_ativo,dosagem,frequencia,data_inicio,data_fim,uso_continuo,observacoes,id_pet,id_consulta",
                continuo ? "[DEMO] Formula continua" : "[DEMO] Formula temporaria",
                "Substancia ficticia", "Dosagem simulada", "Frequencia simulada", inicio, fim, continuo,
                "Somente para testar a interface. Nao utilizar em animais.", pet, consulta);
        evento(pet, consulta, TipoEvento.MEDICAMENTO, "Medicamento ficticio registrado.", inicio, fim);
    }

    private void evento(long pet, Long consulta, TipoEvento tipo, String descricao, LocalDate data, LocalDate proxima) {
        inserir("tb_evento_saude",
                "tipo_evento,descricao,data_evento,data_proxima_acao,concluido,criado_em,id_pet,id_consulta",
                tipo.name(), "[DEMO] " + descricao, data, proxima, proxima == null,
                data.atTime(10, 0), pet, consulta);
    }

    private long inserir(String tabela, String colunas, Object... valores) {
        var chave = new GeneratedKeyHolder();
        String sql = "insert into " + tabela + "(" + colunas + ") values("
                + String.join(",", Collections.nCopies(valores.length, "?")) + ")";
        jdbc.update(conexao -> {
            var comando = conexao.prepareStatement(sql, new String[] {"id"});
            for (int i = 0; i < valores.length; i++) comando.setObject(i + 1, valores[i]);
            return comando;
        }, chave);
        return chave.getKey().longValue();
    }

    private Resumo resumir(boolean criada) {
        return new Resumo(criada,
                contar("select count(*) from tb_clinica where email like 'demo.s4.clinica.%@example.invalid'"),
                contar("select count(*) from tb_tutor where email like 'demo.s4.tutor.%@example.invalid'"),
                contar("select count(*) from tb_pet where microchip like 'DEMO-S4-%'"),
                contarFilhos("tb_consulta"), contarFilhos("tb_vacina"), contarFilhos("tb_medicamento"),
                contarFilhos("tb_evento_saude"));
    }

    private long contarFilhos(String tabela) {
        return contar("select count(*) from " + tabela
                + " f join tb_pet p on p.id=f.id_pet where p.microchip like 'DEMO-S4-%'");
    }

    private long contar(String sql) { return jdbc.queryForObject(sql, Long.class); }

    private static String cpf(String base) {
        String primeiro = base + digito(base, new int[] {10,9,8,7,6,5,4,3,2});
        return primeiro + digito(primeiro, new int[] {11,10,9,8,7,6,5,4,3,2});
    }

    private static String cnpj(String base) {
        String primeiro = base + digito(base, new int[] {5,4,3,2,9,8,7,6,5,4,3,2});
        return primeiro + digito(primeiro, new int[] {6,5,4,3,2,9,8,7,6,5,4,3,2});
    }

    private static int digito(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) soma += (base.charAt(i) - '0') * pesos[i];
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
