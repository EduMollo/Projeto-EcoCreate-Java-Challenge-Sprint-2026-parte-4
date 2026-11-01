-- Apenas leitura. Executar no SQL Editor após iniciar o perfil postgres.
SELECT installed_rank,version,description,success FROM clyvovet.flyway_schema_history ORDER BY installed_rank;
SELECT schemaname,tablename,rowsecurity FROM pg_catalog.pg_tables WHERE schemaname='clyvovet';
SELECT id,nome,peso_kg,ativo FROM clyvovet.tb_pet ORDER BY id;
SELECT id,status,valor,data_consulta FROM clyvovet.tb_consulta ORDER BY id;
SELECT clyvovet.exportar_atendimentos(NULL);
SELECT clyvovet.faturamento_anual(extract(year from current_date)::integer);
SELECT id_registro,tipo_operacao,data_hora,valor_anterior,valor_novo FROM clyvovet.aud_pet ORDER BY id_auditoria;
SELECT has_schema_privilege('anon','clyvovet','USAGE') AS anon_tem_acesso,
 has_schema_privilege('authenticated','clyvovet','USAGE') AS authenticated_tem_acesso;
