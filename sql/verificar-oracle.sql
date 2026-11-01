-- Somente leitura, apos instalacao Oracle Sprint 4 e operacoes pela interface.
SELECT name,type,line,position,text FROM user_errors
WHERE name LIKE 'PKG_CLYVO%' OR name IN ('TRG_CV4_AUD_PET','TRG_CV4_CONSULTA_CAT') ORDER BY name,sequence;
SELECT object_name,object_type,status FROM user_objects
WHERE object_name LIKE 'PKG_CLYVO%' OR object_name IN ('TRG_CV4_AUD_PET','TRG_CV4_CONSULTA_CAT');
SELECT id,nome,peso_kg,ativo FROM tb_pet ORDER BY id;
SELECT id_pet,nome,peso_kg FROM CV4_PET ORDER BY id_pet;
SELECT id,status,valor,data_consulta,id_veterinario,id_tipo_servico FROM tb_consulta ORDER BY id;
SELECT id_atendimento,status,valor,data_atendimento FROM CV4_ATENDIMENTO ORDER BY id_atendimento;
SELECT id_auditoria,id_registro,tipo_operacao,nome_usuario,data_hora,valor_anterior,valor_novo FROM CV4_AUD_PET ORDER BY id_auditoria;