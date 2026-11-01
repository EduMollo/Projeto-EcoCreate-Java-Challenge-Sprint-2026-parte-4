# Integracao Oracle — Database Sprint 4 (atualizado em 01/11/2026)

A implementacao atual esta em `src/main/resources/db/oracle-sprint4`. O perfil Oracle usa `TB_*` para o JPA, views `CV4_*` sobre as mesmas linhas e tabelas auxiliares `CV4_VETERINARIO`, `CV4_TIPO_SERVICO`, `CV4_LEITURA_SENSOR` e `CV4_AUD_PET`.

Os diretorios `db/oracle` e a documentacao anterior descrevem uma primeira preparacao da integracao; nao sao o caminho ativo da entrega Database atual. A Sprint 3 original permanece preservada.

## Conta Oracle FIAP

Usar os scripts da pasta irma `CS4 DB/EcoCreate-BD-Challenge-Sprint-2026-parte-4`:

```powershell
.\configurar-oracle.ps1
.\executar-oracle.ps1 check
.\executar-oracle.ps1 install
.\executar-oracle.ps1 seed
.\executar-oracle.ps1 test
.\executar-oracle.ps1 export
.\executar-oracle.ps1 app
```

A instalacao manual protege os objetos da Sprint 3 por meio dos prefixos CV4, inclusive nos nomes de constraints e indices. Ela interrompe se os objetos desta versao ja existirem. Nunca executar o script de limpeza da Sprint 3 sobre este projeto.

O comando app define `SPRING_PROFILES_ACTIVE=oracle`, `COOKIE_SECURE=false` para HTTP local e `ORACLE_FLYWAY_ENABLED=false`, pois o schema foi instalado manualmente. O acesso e lido do `.env` local da entrega Database; nao versionar credenciais.

Alternativa: em schema novo sem objetos da aplicacao, deixar `ORACLE_FLYWAY_ENABLED=true` para o Flyway usar `db/oracle-sprint4` e `CV4_FLYWAY_HISTORY`. Nao misturar instalacao manual e Flyway na mesma base. A carga demonstrativa 09 continua separada das migrations.

## Demonstracao real

1. Cadastrar um pet pela aplicacao e conferir o mesmo ID em TB_PET e CV4_PET.
2. Agendar com veterinario do catalogo, iniciar e concluir atendimento; conferir TB_CONSULTA e CV4_ATENDIMENTO.
3. Em /relatorios, exportar o JSON e consultar faturamento. RelatorioOracleService chama as procedures empacotadas com OUT CLOB.
4. Em /relatorios/oracle, registrar leitura via PRC_REGISTRAR_LEITURA, consultar FN_CALC_RISCO_PET e a auditoria.
5. Atualizar um pet e conferir OLD/NEW em CV4_AUD_PET. Desativacao e UPDATE; nao apresentar como DELETE fisico.
6. Salvar o JSON baixado como dataset/2TDSPF_2026_Atendimentos.json na entrega Database.
7. Executar os scripts MongoDB da entrega Database e conferir conteudo e reimportacao.

## Validacao executada

Oracle FIAP 19.3, compilacao PL/SQL, testes transacionais, exportacao de 34 documentos, importacao/reimportacao MongoDB e build completo foram aprovados em 01/11/2026. Foram 38 testes Java executados com sucesso, incluindo os 2 testes OracleIntegracaoTest; 2 testes opcionais PostgreSQL foram ignorados. Consultar Database/docs/2TDSPF_2026_Validacao.json.

Ainda pendentes: conferir cadastro, atendimento e leituras pela interface Oracle e gravar o video narrado. O perfil PostgreSQL publicado continua separado e nao executa packages Oracle.
